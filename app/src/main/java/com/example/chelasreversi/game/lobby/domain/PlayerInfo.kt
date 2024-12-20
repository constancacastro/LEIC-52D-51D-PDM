import android.os.Parcelable
import com.example.chelasreversi.preferences.domain.UserInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerInfo(
    val info: UserInfo,
    val id: String
) : Parcelable
