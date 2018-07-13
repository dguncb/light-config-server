

DROP TABLE IF EXISTS config_value;
DROP TABLE IF EXISTS config_secret;
DROP TABLE IF EXISTS config_service;


CREATE TABLE config_value (
  config_key VARCHAR(256) NOT NULL,
  config_value VARCHAR(256) NOT NULL,
  service_id VARCHAR(256) NOT NULL,
  PRIMARY KEY (config_key, service_id)
);



CREATE TABLE config_secret (
  config_key VARCHAR(256) NOT NULL,
  config_secret_hash VARCHAR(256) NOT NULL,
  config_secret_salt VARCHAR(256) NOT NULL,
  service_id VARCHAR(256) NOT NULL,
  PRIMARY KEY (config_key, service_id)
);


CREATE TABLE config_service (
  service_id VARCHAR(256) NOT NULL,
  encryptio_algorithm VARCHAR(32) NOT NULL,
  encryption_salt VARCHAR(256) NOT NULL,
  template_repository VARCHAR(256) ,
  service_owner VARCHAR(32),
  PRIMARY KEY ( service_id)
);

