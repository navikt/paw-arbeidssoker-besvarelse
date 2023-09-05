package no.nav.paw.besvarelse.services

import io.mockk.every
import io.mockk.mockk
import no.nav.paw.besvarelse.BesvarelseTestData
import no.nav.paw.besvarelse.BesvarelseTestData.arbeidssokerRegistrert
import no.nav.paw.besvarelse.BesvarelseTestData.pdlClientHentIdenterResponse
import no.nav.paw.besvarelse.kafka.producer.ArbeidssokerBesvarelseProducer
import no.nav.paw.besvarelse.repository.ArbeidssokerRegistrertRepository
import no.nav.paw.besvarelse.utils.TestDatabase
import no.nav.paw.besvarelse.utils.mockPdlClient
import no.nav.paw.besvarelse.utils.objectMapper
import org.junit.jupiter.api.BeforeEach
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

class ArbeidssokerRegistrertServiceTest {
    private lateinit var dataSource: DataSource
    private lateinit var repository: ArbeidssokerRegistrertRepository

    @BeforeEach
    fun setUp() {
        dataSource = TestDatabase.setupDataSource(TestDatabase.setup())
        repository = ArbeidssokerRegistrertRepository(dataSource, objectMapper)
    }

    @Test
    fun opprettArbeidssokerRegistrert() {
        val arbeidssokerBesvarelseProducer = mockk<ArbeidssokerBesvarelseProducer>()
        every { arbeidssokerBesvarelseProducer.publish(any()) } returns Unit

        val arbeidssokerRegistrertService = ArbeidssokerRegistrertService(repository, arbeidssokerBesvarelseProducer, mockPdlClient(pdlClientHentIdenterResponse))
        arbeidssokerRegistrertService.opprettArbeidssokerRegistrert(arbeidssokerRegistrert)

        val sistBesvarelse = arbeidssokerRegistrertService.hentSiste(BesvarelseTestData.foedselsnummer)

        assertEquals(1, sistBesvarelse.registreringsId)
    }

    @Test
    fun `opprettArbeidssokerRegistrert med to fødselsnummer`() {
        val arbeidssokerBesvarelseProducer = mockk<ArbeidssokerBesvarelseProducer>()
        every { arbeidssokerBesvarelseProducer.publish(any()) } returns Unit

        val arbeidssokerRegistrertService = ArbeidssokerRegistrertService(repository, arbeidssokerBesvarelseProducer, mockPdlClient(pdlClientHentIdenterResponse))

        val sistBesvarelse = arbeidssokerRegistrertService.hentSiste(BesvarelseTestData.foedselsnummer2)

        assertEquals(1, sistBesvarelse.registreringsId)
    }
}
