package proyecto.com.Razonamiento_A_B.servicio;

import javax.persistence.NoResultException;

import org.openxava.jpa.XPersistence;

import proyecto.com.Razonamiento_A_B.enums.Factor;
import proyecto.com.Razonamiento_A_B.modelo.BaremoRazonamiento;

public class BaremoRazonamientoService {

    private BaremoRazonamientoService() {
    }

    public static BaremoRazonamiento buscarBaremo(Factor factor, Integer puntaje) {
        if (factor == null || puntaje == null) {
            return null;
        }

        try {
            return XPersistence.getManager()
                    .createQuery(
                            "from BaremoRazonamiento b " +
                                    "where b.factor = :factor " +
                                    "and :puntaje between b.puntajeMin and b.puntajeMax",
                            BaremoRazonamiento.class
                    )
                    .setParameter("factor", factor)
                    .setParameter("puntaje", puntaje)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}