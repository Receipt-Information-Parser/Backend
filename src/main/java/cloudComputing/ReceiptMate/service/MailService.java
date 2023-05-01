package cloudComputing.ReceiptMate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendPasswordMail(String mail, String password) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mail);
        message.setSubject("[Receipt Mate] 재설정된 임시 비밀번호 전송");
        message.setText(
                        "귀하의 Receipt Mate 계정 비밀번호를 재설정하였습니다!\n" +
                        "하단의 비밀번호를 입력해주세요\n\n" +
                        "-------------------------\n" +
                        password +
                        "\n-------------------------"
        );
        javaMailSender.send(message); // 일반적인 예외를 처리해주고 있는 듯?
    }
}
