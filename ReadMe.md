# Kodluyoruz & Solmaz Gümrük Müşavirliği

# Bootcamp Bitirme Projesi

Spring Boot  Project

 <a href="https://dev.java/" rel="nofollow"> <img src="https://camo.githubusercontent.com/4516a1dca56d6cc15e4102e39acf0c139cc69f220d05b9136af0dfece96a3dfd/68747470733a2f2f75706c6f61642e77696b696d656469612e6f72672f77696b6970656469612f74722f322f32652f4a6176615f4c6f676f2e737667" alt="nodejs" width="40" height="40" data-canonical-src="https://upload.wikimedia.org/wikipedia/tr/2/2e/Java_Logo.svg" style="max-width: 100%;"> </a> <a href="https://spring.io/" rel="nofollow"> <img src="https://camo.githubusercontent.com/4545b55c7771bbd175235c80b518dcbbf2f6ee0b984a51ad9363cba8cb70e67c/68747470733a2f2f7777772e766563746f726c6f676f2e7a6f6e652f6c6f676f732f737072696e67696f2f737072696e67696f2d69636f6e2e737667" alt="spring" width="40" height="40" data-canonical-src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" style="max-width: 100%;"> </a> <a href="https://www.postgresql.org" rel="nofollow"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/postgresql/postgresql-original-wordmark.svg" alt="postgresql" width="40" height="40" style="max-width: 100%;"> </a> <a href="https://postman.com" rel="nofollow"> <img src="https://camo.githubusercontent.com/93b32389bf746009ca2370de7fe06c3b5146f4c99d99df65994f9ced0ba41685/68747470733a2f2f7777772e766563746f726c6f676f2e7a6f6e652f6c6f676f732f676574706f73746d616e2f676574706f73746d616e2d69636f6e2e737667" alt="postman" width="40" height="40" data-canonical-src="https://www.vectorlogo.zone/logos/getpostman/getpostman-icon.svg" style="max-width: 100%;"> </a>


 ```
  - Java 17
  - Maven
  - Java Spring Boot
  - Postman
  - PostgreSQL
  - RabbitMQ
  - MongoDB
  - Swagger
  ```

<hr>

## PROJE KONUSU

Online uçak ve otobüs bileti satışı yapılmak istenmektedir. Uygulamanın gereksinimleri
aşağıdaki gibidir.
## Gereksinimler:

- Kullanıcılar sisteme kayıt ve login olabilmelidir.
- Kullanıcı kayıt işleminden sonra mail gönderilmelidir.
- Kullanıcı şifresi istediğiniz bir hashing algoritmasıyla database kaydedilmelidir.
- Admin kullanıcı yeni sefer ekleyebilir, iptal edebilir, toplam bilet satışını, bu satıştan
  elde edilen toplam ücreti görebilir.
- Kullanıcılar şehir bilgisi, taşıt türü(uçak & otobüs) veya tarih bilgisi ile tüm seferleri
  arayabilmelidir.
- Kullancıların aldıkları ürünler ödeme işlemi başarılı olduktan sonra tanımlanmalı ve bu işlem asenkron yapılmalı.
- Bireysel kullanıcı aynı sefer için en fazla 5 bilet alabilir.
- Bireysel kullanıcı tek bir siparişte en fazla 2 erkek yolcu için bilet alabilir.
- Kurumsal kullanıcı aynı sefer için en fazla 20 bilet alabilir.
- Satın alma işlemi başarılı ise işlem tamamlanmalı ve asenkron olarak bilet detayları
  kullanıcının telefona numarasına sms gönderilmeli.
- SMS, mail ve push Notification gönderme işlemleri için sadece Database kayıt etme
  işlemi yapılması yeterlidir. Fakat bu işlemler tek bir Servis(uygulama) üzerinden ve
  polimorfik davranış ile yapılmalıdır.
- Kullancılar aldığı biletleri görebilmelidir.

<br>

## Sistem Kabulleri:

- Kullanıcılar bireysel ve kurumsal olabilir.
- SMS, Mail ve Push Notification gönderim işlemleri Asenkron olmalıdır.
- Uçak yolcu kapasitesi: 189
- Otobüs yolcu kapasitesi: 45
- Ödeme şekli sadece Kredi kartı ve Havale / EFT olabilir.
- Ödeme Servisi işlemleri Senkron olmalıdır.

