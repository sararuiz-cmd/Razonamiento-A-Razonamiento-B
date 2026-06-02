package proyecto.com.Razonamiento_A_B;

import jakarta.persistence.EntityManager;
import proyecto.com.Razonamiento_A_B.configuracion.JPAUtil;
import proyecto.com.Razonamiento_A_B.enums.NivelAcademico;
import proyecto.com.Razonamiento_A_B.enums.Sexo;
import proyecto.com.Razonamiento_A_B.modelo.Evaluado;
import proyecto.com.Razonamiento_A_B.modelo.Evaluador;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Evaluador evaluador = new Evaluador();
            evaluador.setNombres("Ana María");
            evaluador.setApellidos("López");
            evaluador.setFechaNacimiento(LocalDate.of(1990, 5, 10));
            evaluador.setSexo(Sexo.FEMENINO);
            evaluador.setProfesion("Psicóloga");

            Evaluado evaluado = new Evaluado();
            evaluado.setNombres("Carlos José");
            evaluado.setApellidos("Pérez");
            evaluado.setFechaNacimiento(LocalDate.of(2008, 4, 15));
            evaluado.setSexo(Sexo.MASCULINO);
            evaluado.setNivelAcademico(NivelAcademico.SECUNDARIA);

            em.persist(evaluador);
            em.persist(evaluado);

            em.getTransaction().commit();

            System.out.println("Datos guardados correctamente.");
            System.out.println("Evaluado: " + evaluado.getNombreCompleto());
            System.out.println("Edad: " + evaluado.getEdad());
            System.out.println("Rol: " + evaluado.getRolSistema());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.out.println("Error al guardar datos:");
            e.printStackTrace();

        } finally {
            em.close();
            JPAUtil.cerrar();
        }
    }
}