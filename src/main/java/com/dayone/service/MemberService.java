package com.dayone.service;

import com.dayone.model.Auth;
import com.dayone.persist.entity.MemberEntity;
import com.dayone.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return this.memberRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "couldn't find user -> " + username)
                );

    }
    public MemberEntity register(Auth.SignUp member){
        boolean exists =
                this.memberRepository.existsByUserName(
                        member.getUsername());

        if(exists){
            throw new RuntimeException("이미 사용중인 아이디 입니다.");
        }

        member.setPassword(
                this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());
        return result;
    }
    public MemberEntity authenticate(Auth.SignIn member){
        return null;
    }
}
