ALTER TABLE arbeidssoker_registrert
    RENAME COLUMN registrerings_dato TO registrerings_tidspunkt;
ALTER TABLE arbeidssoker_registrert
    RENAME COLUMN endret_dato TO endret_tidspunkt;
ALTER TABLE arbeidssoker_registrert
    RENAME COLUMN endret TO er_besvarelsen_endret;