package proyecto.com.Razonamiento_A_B.run;

import org.openxava.util.*;

/**
 * Ejecuta esta clase para arrancar la aplicación.
 */

public class Razonamiento_A_B {

	public static void main(String[] args) throws Exception {
		DBServer.start("Razonamiento_A_B-db"); // Para usar tu propia base de datos comenta esta línea y configura src/main/webapp/META-INF/context.xml
		AppServer.run("Razonamiento_A_B"); // Usa AppServer.run("") para funcionar en el contexto raíz
	}

}
