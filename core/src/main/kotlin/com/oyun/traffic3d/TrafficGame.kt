package com.oyun.traffic3d

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight

/**
 * Oyunun giriş noktası. Bu sınıf hem Android hem masaüstü tarafından çağrılır.
 * Şu an için: 1 oyuncu arabası (3 model arasından seçilebilir) + sonsuz yol +
 * 3 kamera modu (arka/kaput/iç mekan).
 * Trafik yapay zekası, skor sistemi, şerit değiştirme animasyonu gibi özellikler
 * henüz eklenmedi - bunlar bir sonraki adımlar.
 */
class TrafficGame : ApplicationAdapter() {

    private lateinit var modelBatch: ModelBatch
    private lateinit var environment: Environment
    private lateinit var camera: PerspectiveCamera
    private lateinit var cameraManager: CameraManager
    private lateinit var car: CarController
    private lateinit var road: RoadGenerator

    // Seçilebilir araba modelleri - yeni araba eklemek için buraya bir satır eklemen yeterli.
    private val carModelPaths = listOf(
        "models/suzuki_gsx_750_bike_3d_model.glb",
        "models/2022_bmw_m440i_xdrive_gran_coupe.glb",
        "models/2014_lbworks_bmw_4_series_body_kit_f82.glb"
    )
    private var currentCarIndex = 0

    override fun create() {
        modelBatch = ModelBatch()

        environment = Environment().apply {
            set(ColorAttribute(ColorAttribute.AmbientLight, 0.55f, 0.55f, 0.55f, 1f))
            add(DirectionalLight().set(0.9f, 0.9f, 0.85f, -0.4f, -0.9f, -0.3f))
        }

        camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
            near = 0.1f
            far = 300f
        }

        road = RoadGenerator()

        // models/*.glb dosyaları assets altında yoksa otomatik olarak kırmızı bir
        // kutu (placeholder) render edilir, böylece model eklemeden de test edebilirsin.
        loadCar(currentCarIndex)
    }

    private fun loadCar(index: Int) {
        val previousPosition = if (::car.isInitialized) car.position else null

        car = CarController(ModelLoader.loadCarOrPlaceholder(carModelPaths[index]))

        // Araba değiştirince aynı yerden devam etsin, sıfırlanmasın.
        previousPosition?.let { car.position.set(it) }

        cameraManager = CameraManager(camera, car)
    }

    private fun nextCar() {
        currentCarIndex = (currentCarIndex + 1) % carModelPaths.size
        loadCar(currentCarIndex)
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        handleInput(delta)
        car.update(delta)
        road.update(car.position.z)
        cameraManager.update()

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0.4f, 0.65f, 0.9f, 1f) // gökyüzü rengi (şimdilik düz renk)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        modelBatch.begin(camera)
        modelBatch.render(car.modelInstance, environment)
        road.render(modelBatch, environment)
        modelBatch.end()
    }

    private fun handleInput(delta: Float) {
        // Basit dokunmatik kontrol: ekranın solu = sola dön, sağı = sağa dön.
        // Gaz sürekli basılı gibi davranıyor; fren/gaz pedalını sonra ayrı ekleyeceğiz.
        if (Gdx.input.isTouched) {
            val touchX = Gdx.input.x
            if (touchX < Gdx.graphics.width / 2) car.steerLeft(delta) else car.steerRight(delta)
        }
        car.accelerate(delta)

        // Test için: masaüstünde boşluk tuşuyla kamera modu değiştir.
        // Android'de bunu ekranda bir "kamera değiştir" butonuna bağlayacağız.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            cameraManager.nextMode()
        }

        // Test için: masaüstünde TAB tuşuyla araba değiştir.
        // Android'de bunu ekranda bir "araba değiştir" butonuna bağlayacağız.
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            nextCar()
        }
    }

    override fun dispose() {
        modelBatch.dispose()
        road.dispose()
    }
}
