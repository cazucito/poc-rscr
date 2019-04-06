package wse;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/rscr")
public class VariosClientesWSEndpoint {

    private static Set<Session> clientes = Collections.synchronizedSet(new HashSet<Session>());
    private static Map<String, Session> usuariosConectados = new HashMap<>();

    @OnMessage
    public void onMessage(String _msj, Session _sesion) throws IOException {
        // {type:"10",mensaje="MensajeGlobalPrueba"}
        String tipo = _msj.substring(9, 11);
        String mensaje = _msj.substring(24, _msj.length() - 2);
        //System.out.println("tipo:" + tipo);
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
            // mensaje particular
            // {type:"12",mensaje="MensajeGlobalPrueba"}
            case "12":
                //System.out.println("mensaje:" + mensaje);
                String aQuien = mensaje.substring(mensaje.indexOf("@") + 1, mensaje.length());
                mensaje = mensaje.substring(0, mensaje.indexOf("@"));
                System.out.println("aQuien:" + aQuien);
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                sb.append("\"type\":").append("\"11\"");
                sb.append(",");
                sb.append("\"mensaje\":").append("\"").append(mensaje).append("\"");
                sb.append("}");
                for (Session cliente : clientes) {
                    System.out.println("cliente.getId():"+cliente.getId());
                    if (cliente.getId().equalsIgnoreCase(aQuien)) {
                        cliente.getBasicRemote().sendText(sb.toString());
                        break;
                    }
                }
                break;
            case "30":
                usuariosConectados.put(mensaje, _sesion);
                // {type:"31",mensaje=""}
                sb = new StringBuilder();
                sb.append("{");
                sb.append("\"type\":").append("\"31\"");
                sb.append(",");

                sb.append("\"mensaje\":");
                sb.append("[");
                // [{"alias":"juan",sesion="123"}, {"alias":"juan",sesion="123"}]
                Set<String> alias = usuariosConectados.keySet();
                int contador = 0;
                for (String s : alias) {
                    sb.append("{");
                    sb.append("\"alias\":").append("\"").append(s).append("\"");
                    sb.append(",");
                    sb.append("\"sesion\":").append("\"").append((usuariosConectados.get(s)).getId()).append("\"");
                    sb.append("}");
                    contador++;
                    if (contador < alias.size()) {
                        sb.append(",");
                    }
                }
                sb.append("]");
                sb.append("}");
                //System.out.println("usuarios:"+sb.toString());
                for (Session cliente : clientes) {
                    // if (!cliente.equals(_sesion)) {
                    cliente.getBasicRemote().sendText(sb.toString());
                    // }
                }
                break;
            // MENSAJE NO CONOCIDO
            default:
                System.out.println("MENSAJE DESCONOCIDO");

        }
    }

    @OnOpen
    public void onOpen(Session session) {
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
