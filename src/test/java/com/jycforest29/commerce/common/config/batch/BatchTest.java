//package com.jycforest29.commerce.common.config.batch;
//
//import com.jycforest29.commerce.order.domain.entity.MadeOrder;
//import com.jycforest29.commerce.order.domain.repository.MadeOrderRepository;
//import com.jycforest29.commerce.user.domain.entity.AuthUser;
//import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.batch.core.ExitStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.test.JobLauncherTestUtils;
//import org.springframework.batch.test.context.SpringBatchTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import org.springframework.data.auditing.AuditingHandler;
//import org.springframework.data.auditing.DateTimeProvider;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.BDDMockito.given;
//
//@Slf4j
////@ActiveProfiles(profiles = "test")
////@SpringBootTest(properties = "spring.profiles.active:test")
//@SpringBootTest
//@SpringBatchTest
//public class BatchTest {
//    @Autowired
//    private JobLauncherTestUtils jobLauncherTestUtils;
//    @Autowired
//    private MadeOrderRepository madeOrderRepository;
//    @Autowired
//    private AuthUserRepository authUserRepository;
//    @MockBean
//    private DateTimeProvider dateTimeProvider;
//    @SpyBean
//    AuditingHandler auditingHandler;
//    MadeOrder madeOrder;
//
//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.openMocks(this);
//        auditingHandler.setDateTimeProvider(dateTimeProvider);
//    }
//
//    @AfterEach
//    void endUp(){
//        madeOrderRepository.deleteAll();
//        authUserRepository.deleteAll();
//    }
//
//    @Test
//    void 잡_종료가_정상적으로_이루어졌다() throws Exception {
//        // given
//        given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.now().minusDays(5)));
//        AuthUser authUser = AuthUser.builder()
//                .username("test_username")
//                .password("test_password")
//                .nickname("test_nickname")
//                .build();
//        authUserRepository.save(authUser);
//        madeOrder = madeOrderRepository.save(
//                MadeOrder.builder()
//                        .authUser(authUser)
//                        .build()
//        );
//        // when
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
//        // then
//        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
//        assertThat(madeOrder.getCancelAvaiable()).isEqualTo(false);
//    }
//}
