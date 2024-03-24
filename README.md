# Spring Boot 3 with Spring Security 6. An example of JWT Authentication and Authorisation

### Description

For further reference, please consider the following sections:

* The database layer is PostgreSQL
    * Yaml file with parameters: application.yml
    * Lombok's annotations: @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
    * JPA @Entity and @Id
    * Repository extending JpaRepository<User, Long>. Implementing findByEmail().

* Spring Security
    * Implementing UserDetails with GrandtedAuthorities, username(email), password.
    * Extracting Username from Bearer Token using 3rd Party Libs
    * generating signature key
    * SecurityFilterChain with  .requestMatchers("/api/v1/auth/**")
                                .permitAll()
                                .anyRequest().authenticated()
    * Check is token is valid< Expired?
    * UserDetails and authenticationManager with new UsernamePasswordAuthenticationToken()

* Controller
  * Register
  * Login/Authenticate

