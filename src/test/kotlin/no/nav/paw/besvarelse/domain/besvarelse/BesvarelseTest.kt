package no.nav.paw.besvarelse.domain.besvarelse

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BesvarelseTest {

    @Test
    fun `sisteEndret - returnerer null når tom besvarelse`() {
        assertEquals(null, Besvarelse().sisteEndret())
    }

    @Test
    fun `sisteEndret - returnerer endret dato`() {
        val endret = LocalDateTime.now()
        val besvarelse = Besvarelse(
            Utdanning(UtdanningSvar.GRUNNSKOLE, endret = endret)
        )

        assertEquals(endret, besvarelse.sisteEndret())
    }

    @Test
    fun `sisteEndret - returnerer nyeste endret dato`() {
        val endret1 = LocalDateTime.of(2023, 5, 8, 12, 12, 12)
        val endret2 = LocalDateTime.of(2023, 5, 9, 12, 12, 12)

        val besvarelse = Besvarelse(
            Utdanning(UtdanningSvar.GRUNNSKOLE, endret = endret1),
            dinSituasjon = DinSituasjon(DinSituasjonSvar.ER_PERMITTERT, endret = endret2)
        )

        assertEquals(endret2, besvarelse.sisteEndret())
    }
}
