ALTER TABLE arbeidssoker_registrert
    RENAME COLUMN registrering_opprettet TO registrerings_dato;
ALTER TABLE arbeidssoker_registrert
    RENAME COLUMN opprettet TO endret_dato;
