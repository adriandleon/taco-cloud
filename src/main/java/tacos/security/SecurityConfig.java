package tacos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tacos.User;
import tacos.data.UserRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        List<UserDetails> usersList = new ArrayList<>();
//        usersList.add(
//                new User("buzz", encoder.encode("password"), List.of(new SimpleGrantedAuthority("ROLE_USER")))
//        );
//        usersList.add(
//                new User("woody", encoder.encode("password"), List.of(new SimpleGrantedAuthority("ROLE_USER")))
//        );
//
//        return new InMemoryUserDetailsManager(usersList);
//    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username);
            if (user != null) return user;

            throw new UsernameNotFoundException("User '" + username + "' not found");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/design", "/orders").hasRole("USER")
                .requestMatchers("/", "/**").permitAll()
        ).formLogin(formLogin -> formLogin
                .loginPage("/login")
        ).oauth2Login(oAuth2Login ->
                oAuth2Login.loginPage("/login")
        ).logout( logout ->
                logout.logoutSuccessUrl("/")
        ).build();
    }
}
