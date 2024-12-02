//package service;
//
//import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class JwtServiceTest {
//
//    private final JwtService jwtService;
//
//    public JwtServiceTest() {
//        jwtService = new JwtService();
//        jwtService.setSecretKey("cc1ac5a9aa840a38af2dc45a8a7480dc64e3d179f9dd6339836cdc1680776e45f592c34d53b2581ffa4c1993d2469fe28ab6ac1932da57bfc638eac0c1577acc339a3292560242ba7b9da8558a3bb0e7c52145a6300233488ae46e7f1b199d961fbb77c82f7627cbb557554bfab45a40a35a5671c586a15099c06622a9cac884bf1151f722eeed095ca3e075a856380ec5410f972140181050162a1eb2ab043d3ab678f04ad0be79654028c441150e799400fdca434fb1492f08a018220d37098a44bfdd28418d69600c7b73d0187d6c177d2b5c2e0c579c773558409e3c0f733cd78134f472d631a9df231d5b955c2c40d31f67629523169cc555a499399d29");
//        jwtService.setJwtExpiration(900000); // 15 minutes
//    }
//
//    @Test
//    void testGenerateAndValidateToken() {
//        // Mock UserDetails
//        UserDetails userDetails = User.builder()
//                .username("testuser")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        // Générer le token
//        String token = jwtService.generateToken(userDetails);
//        assertThat(token).isNotNull();
//
//        // Valider le token
//        boolean isValid = jwtService.isTokenValid(token);
//        assertThat(isValid).isTrue();
//
//        // Extraire le nom d'utilisateur
//        String username = jwtService.extractUsername(token);
//        assertThat(username).isEqualTo("testuser");
//    }
//
//    @Test
//    void testTokenExpiration() throws InterruptedException {
//        // Mock UserDetails
//        UserDetails userDetails = User.builder()
//                .username("testuser")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        // Générer un token avec une expiration très courte
//        long shortExpiration = 1000; // 1 seconde
//        jwtService.setJwtExpiration(shortExpiration);
//        String token = jwtService.generateToken("testuser", userDetails);
//
//        // Attendre que le token expire
//        Thread.sleep(shortExpiration + 100);
//
//        // Valider que le token est expiré
//        boolean isValid = jwtService.isTokenValid(token);
//        assertThat(isValid).isFalse();
//    }
//}