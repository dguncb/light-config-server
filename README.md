# Light Config Server


### Service build as Light Hybrid 4J Service

This project is  developed as light-hybrid-4j services.  To deploy your service, just drop
the jar into a directory and start light-hybrid-4j server.


### Config Server


Services built on the light platform are composed with many plugins as part of the embedded gateway, and each plugin(or middleware handler)
has its configuration file to control if they are enabled and the runtime behavior.
Developers usually are don’t need to worry about these config files as these can be added later on by operation team.


Unlike other application frameworks, we are not using a single config file but multiple config files as each service decides which plugins should be utilized and how they are utilized.


When managing the configurations, the following things need to be considered:


-- Multiple config template files per service.

-- Every environment has some values different.

-- Some sensitive values need to be encrypted.

-- Some of the config files need to be merged from several organizations.

-- Some of the config files need to be overwritten from several organizations.

-- When config server is used, there should b minimum internal config files to ensure the connectivity to the config server



### Config Value data format

The config value will be save into database (NoSql DB or RDBMS DB). The config value will have two type entries:

-- Service specific config value(identify by service Id)

-- Common config value, which is same for all services in the environment.


From config value key, system identify the template and and config name in the confgi template file.

For example:

config key:  service/javax.sql.DataSource/com.zaxxer.hikari.HikariDataSource/DriverClassName

It will identify following config in the service.yml file (template):

javax.sql.DataSource
  com.zaxxer.hikari.HikariDataSource
    DriverClassName


### Workflow diagram