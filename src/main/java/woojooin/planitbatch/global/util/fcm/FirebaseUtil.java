package woojooin.planitbatch.global.util.fcm;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushFcmOptions;
import com.google.firebase.messaging.WebpushNotification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirebaseUtil {
	private final FirebaseMessaging messaging;

	/**
	 * FCM 기반 특정 사용자에게 알림 보내는 메서드
	 * @param token : client에서 발급받은 vapidKey
	 * @param title : 알림 제목
	 * @param body : 알림 내용
	 * @param link : 알림 클릭시 이동할 링크
	 * @param data : front-end에서 받아서 처리할 데이터 (key-value형태로 json 형태로 전송됨)
	 */
	public String sendToWebToken(String token, String title, String body, String link, Map<String, String> data) throws
		Exception {
		WebpushNotification notification = WebpushNotification.builder()
			.setTitle(title)
			.setBody(body)
			.setIcon("/icons/icon-192.png")
			.build();

		WebpushConfig webpush = WebpushConfig.builder()
			.setNotification(notification)
			.setFcmOptions(WebpushFcmOptions.withLink(link)) // 클릭 시 열 URL
			.putHeader("TTL", "86400") // 24h
			.build();

		Message.Builder builder = Message.builder()
			.setToken(token)
			.setWebpushConfig(webpush);

		if (data != null && !data.isEmpty()) {
			data.forEach(builder::putData);
		}

		return messaging.send(builder.build());
	}

}
