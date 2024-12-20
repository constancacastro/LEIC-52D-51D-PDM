package com.example.chelasreversi.game.lobby.adapters

import PlayerInfo
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.lobby.domain.ChallengeReceived
import com.example.chelasreversi.game.lobby.domain.Lobby
import com.example.chelasreversi.game.lobby.domain.LobbyEvent
import com.example.chelasreversi.game.lobby.domain.RosterUpdated
import com.example.chelasreversi.preferences.domain.UserInfo
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UnreachableLobbyException : Exception()

class LobbyFirebase(private val db: FirebaseFirestore) : Lobby {

    private var state: LobbyState = Idle

    private suspend fun addLocalPlayer(localPlayer: PlayerInfo): DocumentReference {

        val userDocRef = db.collection("users").document(localPlayer.id)
        val lobbySubcollectionRef =
            userDocRef.collection("lobby").document(localPlayer.id)


        userDocRef.set(localPlayer.toDocumentContent()).await()
        lobbySubcollectionRef.set(localPlayer.toDocumentContent()).await()

        return lobbySubcollectionRef
    }

    private fun subscribeRoasterUpdated(flow: ProducerScope<LobbyEvent>) =
        db.collection(LOBBY).addSnapshotListener { snapshot, error ->
            when {
                error != null -> flow.close(error)
                snapshot != null -> flow.trySend(RosterUpdated(snapshot.toPlayerList()))
            }
        }

    private fun subscribeChallengeReceived(
        localPlayerDocRef: DocumentReference,
        flow: ProducerScope<LobbyEvent>
    ) = localPlayerDocRef.addSnapshotListener { snapshot, error ->
        when {
            error != null -> flow.close(error)
            snapshot != null -> {
                val challenge: Challenge? = snapshot.toChallengeOrNull()
                if (challenge != null) {
                    flow.trySend(ChallengeReceived(challenge))
                }
            }
        }
    }

    override suspend fun getPlayers(): List<PlayerInfo> {
        try {
            val result = db.collection(LOBBY).get().await()
            return result.map { it.toPlayerInfo() }
        } catch (e: Throwable) {
            throw UnreachableLobbyException()
        }
    }

    override suspend fun enter(localPlayer: PlayerInfo): List<PlayerInfo> {
        check(state == Idle)
        try {
            state = InUse(localPlayer, addLocalPlayer(localPlayer))
            return getPlayers()
        } catch (e: Throwable) {
            throw UnreachableLobbyException()
        }
    }

    override fun enterAndObserve(localPlayer: PlayerInfo): Flow<LobbyEvent> {
        check(state == Idle)
        return callbackFlow {
            state = InUseWithFlow(localPlayer, scope = this)

            var myDocRef: DocumentReference? = null
            var rosterSubscription: ListenerRegistration? = null
            var challengeSubscription: ListenerRegistration? = null
            try {
                myDocRef = addLocalPlayer(localPlayer)
                challengeSubscription = subscribeChallengeReceived(myDocRef, flow = this)
                rosterSubscription = subscribeRoasterUpdated(flow = this)
            } catch (e: Exception) {
                close(e)
            }

            awaitClose {
                rosterSubscription?.remove()
                challengeSubscription?.remove()
                myDocRef?.delete()
            }
        }
    }

    override suspend fun issueChallenge(to: PlayerInfo): Challenge {
        val localPlayer = when (val currentState = state) {
            is Idle -> throw IllegalStateException()
            is InUse -> currentState.localPlayer
            is InUseWithFlow -> currentState.localPlayer
        }

        db.collection(LOBBY)
            .document(to.id)
            .update(CHALLENGER_FIELD, localPlayer.toDocumentContent())
            .await()

        return Challenge(challenger = localPlayer, challenged = to)
    }

    override suspend fun leave() {
        when (val currentState = state) {
            is InUseWithFlow -> currentState.scope.close()
            is InUse -> currentState.localPlayerDocRef.delete().await()
            is Idle -> throw IllegalStateException()
        }
        state = Idle
    }

}

/**
 * Sum type that characterizes the lobby state
 */
private sealed class LobbyState

private class InUse(
    val localPlayer: PlayerInfo,
    val localPlayerDocRef: DocumentReference
) : LobbyState()

private class InUseWithFlow(
    val localPlayer: PlayerInfo,
    val scope: ProducerScope<LobbyEvent>
) : LobbyState()

private object Idle : LobbyState()

/**
 * Names of the fields used in the document representations.
 */
const val LOBBY = "lobby"
const val NICK_FIELD = "nick"
const val CHALLENGER_FIELD = "challenger"
const val CHALLENGER_ID_FIELD = "id"

fun QueryDocumentSnapshot.toPlayerInfo() =
    PlayerInfo(
        info = UserInfo(
            nickname = data[NICK_FIELD] as String
        ),
        id = "1234",
    )

fun PlayerInfo.toDocumentContent() = mapOf(
    NICK_FIELD to info.nickname,
    CHALLENGER_ID_FIELD to id.toString()
)

@Suppress("UNCHECKED_CAST")
fun DocumentSnapshot.toChallengeOrNull(): Challenge? {
    val docData = data
    if (docData != null) {
        val challenger = docData[CHALLENGER_FIELD] as Map<String, String>?
        if (challenger != null) {
            return Challenge(
                challenger = playerInfoFromDocContent(challenger),
                challenged = this.toPlayerInfo()
            )
        }
    }
    return null
}

fun DocumentSnapshot.toPlayerInfo() = PlayerInfo(
    info = UserInfo(
        nickname = data?.get(NICK_FIELD) as String
    ),
    id = "",
)

fun playerInfoFromDocContent(properties: Map<String, Any>) = PlayerInfo(
    info = UserInfo(
        nickname = properties[NICK_FIELD] as String,
    ),
    id = properties[CHALLENGER_ID_FIELD] as String
)

fun QuerySnapshot.toPlayerList() = map { it.toPlayerInfo() }

fun UserInfo.toDocumentContent() = mapOf(
    NICK_FIELD to nickname,
)