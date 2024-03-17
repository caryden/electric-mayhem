package edu.ncssm.ftc.electricmayhem.dashboard

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException

class EMWebSocket(handshakeRequest : NanoHTTPD.IHTTPSession) : NanoWSD.WebSocket(handshakeRequest) {
    override fun onOpen() {
        TODO("Not yet implemented")
    }
    override fun onClose(
        code: NanoWSD.WebSocketFrame.CloseCode?,
        reason: String?,
        initiatedByRemote: Boolean) {
        TODO("Not yet implemented")
    }
    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
        TODO("Not yet implemented")
    }
    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
        TODO("Not yet implemented")
    }
    override fun onException(exception: IOException?) {
        TODO("Not yet implemented")
    }

}