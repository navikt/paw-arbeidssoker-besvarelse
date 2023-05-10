CREATE TABLE arbeidssoker_registrert (
  id SERIAL PRIMARY KEY,
  foedselsnummer VARCHAR(11) NOT NULL,
  aktor_id VARCHAR NOT NULL,
  registrerings_id INT NOT NULL,
  besvarelse JSONB NOT NULL,
  opprettet TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  registrering_opprettet TIMESTAMP(6) NOT NULL,
  opprettet_av VARCHAR(255) NOT NULL,
  endret_av VARCHAR(255) NOT NULL
);

CREATE INDEX arbeidssoker_registrert_foedselsnummer_index ON arbeidssoker_registrert (foedselsnummer);
