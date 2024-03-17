package edu.ncssm.ftc.electricmayhem.dashboard

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.qualcomm.robotcore.util.WebHandlerManager
import com.qualcomm.robotcore.util.WebServer
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.firstinspires.ftc.robotcore.internal.webserver.WebHandler
import org.firstinspires.ftc.robotserver.internal.webserver.MimeTypesUtil
import java.io.IOException


class EMDashboard() {
    companion object {
        private const val TAG = "EMDashboard"

        private var instance: EMDashboard? = null
        @WebHandlerRegistrar
        fun attachWebServer(context: Context?, manager: WebHandlerManager) {
            if (null == manager.webServer) {
                Log.e(TAG, "Unable to attach web server (webserver is null)")
                return
            }
            getDashoard().internalAttachWebServer(manager.webServer!!)
        }
        fun getDashoard() : EMDashboard {
            if(null == instance)
                instance = EMDashboard()
            return instance!!
         }
    }

    private val server: NanoWSD = object : NanoWSD(8001) {
        override fun openWebSocket(handshake: IHTTPSession): WebSocket {
            return EMWebSocket(handshake)
        }
    }
    private fun internalAttachWebServer(webServer: WebServer) {
        val activity = AppUtil.getInstance().activity ?: return
        val webHandlerManager = webServer.webHandlerManager
        val assetManager = activity.assets

        // register the paths to load the dashboard
        webHandlerManager.register("/em", newStaticAssetHandler(assetManager, "index.html"))
        webHandlerManager.register("/em/", newStaticAssetHandler(assetManager, "index.html"))

        addAssetWebHandlers(webHandlerManager, assetManager, "") // add all the assets in the root directory
    }
    private fun newStaticAssetHandler(assetManager: AssetManager, file: String): WebHandler? {
        return WebHandler { session ->
            if (session.method == NanoHTTPD.Method.GET) {
                val mimeType = MimeTypesUtil.determineMimeType(file)
                NanoHTTPD.newChunkedResponse(
                    NanoHTTPD.Response.Status.OK,
                    mimeType, assetManager.open(file)
                )
            } else {
                NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.NOT_FOUND,
                    NanoHTTPD.MIME_PLAINTEXT, ""
                )
            }
        }
    }
    private fun addAssetWebHandlers(
        webHandlerManager: WebHandlerManager,
        assetManager: AssetManager, path: String) {
        try {
            val list = assetManager.list(path) ?: return
            if (list.size > 0) {
                for (file in list) {
                    addAssetWebHandlers(webHandlerManager, assetManager, "$path/$file")
                }
            } else {
                webHandlerManager.register(
                    "/$path",
                    newStaticAssetHandler(assetManager, path)
                )
            }
        } catch (e: IOException) {
            Log.w(TAG, e)
        }
    }
}