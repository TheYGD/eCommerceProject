//package pl.ecommerce.admin.service;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import pl.ecommerce.admin.repository.AccountRepository;
//import pl.ecommerce.exceptions.InvalidUserCredentialsException;
//import pl.ecommerce.repository.UserCredentialsRepository;
//
//@Service("AdminDetailsService")
//@AllArgsConstructor
//@Slf4j
//public class AdminService implements UserDetailsService {
//
//    private AccountRepository accountRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return accountRepository.findByUsername(username)
//                .orElseThrow( () -> new InvalidUserCredentialsException("Invalid username/email or pasword"));
//    }
//
//}
