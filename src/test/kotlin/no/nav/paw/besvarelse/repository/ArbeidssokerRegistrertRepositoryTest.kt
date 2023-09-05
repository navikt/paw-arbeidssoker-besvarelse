package no.nav.paw.besvarelse.repository

import no.nav.paw.besvarelse.BesvarelseTestData
import no.nav.paw.besvarelse.domain.besvarelse.DinSituasjonSvar
import no.nav.paw.besvarelse.domain.besvarelse.EndretAv
import no.nav.paw.besvarelse.utils.*
import no.nav.paw.besvarelse.utils.TestDatabase
import no.nav.paw.besvarelse.utils.TestDatabase.setupDataSource
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

class ArbeidssokerRegistrertRepositoryTest {
    private lateinit var dataSource: DataSource
    private lateinit var repository: ArbeidssokerRegistrertRepository

    @BeforeEach
    fun setUp() {
        dataSource = setupDataSource(TestDatabase.setup())
        repository = ArbeidssokerRegistrertRepository(dataSource, objectMapper)
    }

    @Test
    fun `Oppretter besvarelse og henter siste besvarelse`() {
        val besvarelse = repository.opprett(BesvarelseTestData.bruker, BesvarelseTestData.arbeidssokerRegistrertEntity, true)

        assertEquals(1, besvarelse.id)
    }

    @Test
    fun `Oppretter besvarelse og endrer situasjon`() {
        repository.opprett(BesvarelseTestData.bruker, BesvarelseTestData.arbeidssokerRegistrertEntity, true)

        val hentetBesvarelse = repository.endreSituasjon(BesvarelseTestData.bruker, BesvarelseTestData.endreSituasjonRequest, EndretAv.BRUKER)

        assertEquals(DinSituasjonSvar.ER_PERMITTERT, hentetBesvarelse.besvarelse.dinSituasjon?.verdi)
        assertEquals(LocalDate.now(), hentetBesvarelse.besvarelse.dinSituasjon?.tilleggsData?.oppsigelseDato)
    }
}
