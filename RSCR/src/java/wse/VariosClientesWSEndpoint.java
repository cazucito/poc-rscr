package wse;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/variosclientes")
public class VariosClientesWSEndpoint {

    private static Set<Session> clientes = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public void onMessage(String _msj, Session _sesion) throws IOException {

        // {type:"10",mensaje="MensajeGlobalPrueba"}
        String tipo = _msj.substring(9, 11);
        String mensaje = _msj.substring(24, _msj.length() - 2);

        System.out.println("tipo:" + tipo);
        switch (tipo) {
            case "10":
                synchronized (clientes) {
                    // {type:"11",mensaje=""}
                    StringBuilder sb = new StringBuilder();
                    sb.append("{");
                    sb.append("\"type\":").append("\"11\"");
                    sb.append(",");
                    sb.append("\"mensaje\":").append("\"").append(mensaje).append("\"");
                    sb.append("}");
                    // Itera sobre los usuarios conectados y les envía un mensaje
                    for (Session cliente : clientes) {
                        if (!cliente.equals(_sesion)) {
                            cliente.getBasicRemote().sendText(sb.toString());
                        }
                    }
                }
                break;
            // {type:"listadoUsuarios",mensaje="MensajeGlobalPrueba"}
            case "11":
                System.out.println("TO-DO");
                break;
            // MENSAJE NO CONOCIDO
            default:
                System.out.println("MENSAJE DESCONOCIDO");

        }
    }

    @OnOpen
    public void onOpen(Session session
    ) {
        // Adiciona la sesión del cliente q se conecta
        clientes.add(session);
    }

    @OnClose
    public void onClose(Session session
    ) {
        // Elimina la sesión del cliente q se desconecta
        clientes.remove(session);
    }
}
