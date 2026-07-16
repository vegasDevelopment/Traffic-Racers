# Traffic3D

Termux + Kotlin + GitHub Actions ile geliştirilen, Traffic Racer tarzı 3D araba oyunu iskeleti (libGDX).

## Şu anda çalışan özellikler
- 3D sahne, tek araç (model yoksa kırmızı kutu placeholder)
- Dokunmatik direksiyon (ekranın solu/sağı = sola/sağa dönüş)
- Sürekli hızlanma (gaz/fren pedalı henüz yok)
- 3 kamera modu: arka (chase), kaput üstü (hood), iç mekan/kokpit (interior)
  - Masaüstünde test ederken **boşluk tuşu** ile kamera değiştirilir
- Sonsuz yol illüzyonu (6-8 segment recycle ediliyor, gerçek yol modeli değil)

## Henüz YOK (sıradaki adımlar)
- Trafik arabaları / yapay zeka
- Şerit değiştirme animasyonu, çarpışma tespiti
- Skor, para, araç mağazası
- Gaz/fren butonları (şu an araç sürekli hızlanıyor)
- Ekranda kamera değiştirme butonu (şu an sadece klavye)
- Ses efektleri, motor sesi

## Termux'ta kullanım
```bash
pkg install git
git clone <senin-repo-url>
cd Traffic3D
# Kotlin dosyalarını nano/vim ile düzenle: core/src/main/kotlin/com/oyun/traffic3d/
git add .
git commit -m "değişiklik"
git push
```
Push sonrası GitHub → Actions sekmesinden derlenen APK'yı indirebilirsin.
Termux'ta ./gradlew ÇALIŞTIRMAYA ÇALIŞMA — Android SDK + çok fazla RAM ister, telefon üzerinde
pratik değil. Derlemeyi tamamen GitHub Actions'a bırak.

## 3D araba modeli ekleme
1. Sketchfab'den `.glb` formatında (iç mekanlı) bir araba modeli indir.
2. Dosyayı şu konuma koy: `core/src/main/assets/models/car1.glb`
3. `ModelLoader.kt` içindeki `assetPath` zaten bu dosyayı arıyor — otomatik yüklenecek.
4. Kamera offsetlerini (`CameraManager.kt` içindeki `interiorOffset`, `hoodOffset`)
   modelinin gerçek boyutuna göre ince ayar yap. Sayılar metre cinsinden.

## Proje yapısı
```
Traffic3D/
├── core/           <- Oyun mantığı (platform bağımsız Kotlin kodu)
│   └── src/main/kotlin/com/oyun/traffic3d/
│       ├── TrafficGame.kt       <- Ana render döngüsü
│       ├── CarController.kt     <- Araç fiziği/hareketi
│       ├── CameraManager.kt     <- 3 kamera modu
│       ├── RoadGenerator.kt     <- Sonsuz yol
│       └── ModelLoader.kt       <- 3D model yükleme
├── android/        <- Android launcher + APK ayarları
└── .github/workflows/build.yml  <- Otomatik APK derleme
```
