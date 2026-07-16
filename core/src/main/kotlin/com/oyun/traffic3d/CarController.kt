package com.oyun.traffic3d

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import kotlin.math.cos
import kotlin.math.sin

/**
 * Basitleştirilmiş (arcade tarzı) araba hareketi.
 * Gerçek süspansiyon/tekerlek fiziği yok - Traffic Racer da zaten bunu kullanmıyor,
 * hıza bağlı yumuşak dönüş hissi yeterli.
 */
class CarController(val modelInstance: ModelInstance) {

    val position = Vector3(0f, 0f, 0f)
    var rotationY = 0f // radyan cinsinden, aracın yönü
        private set
    var speed = 0f
        private set

    private val maxSpeed = 40f          // metre/saniye (~144 km/h)
    private val acceleration = 8f
    private val steerRate = 1.6f        // radyan/saniye, tam hızda

    fun accelerate(delta: Float) {
        speed = (speed + acceleration * delta).coerceAtMost(maxSpeed)
    }

    fun brake(delta: Float) {
        speed = (speed - acceleration * 2f * delta).coerceAtLeast(0f)
    }

    fun steerLeft(delta: Float) {
        val speedFactor = (speed / maxSpeed).coerceAtLeast(0.15f)
        rotationY += steerRate * delta * speedFactor
    }

    fun steerRight(delta: Float) {
        val speedFactor = (speed / maxSpeed).coerceAtLeast(0.15f)
        rotationY -= steerRate * delta * speedFactor
    }

    fun update(delta: Float) {
        position.x += sin(rotationY) * speed * delta
        position.z += cos(rotationY) * speed * delta

        modelInstance.transform.setToRotationRad(Vector3.Y, rotationY)
        modelInstance.transform.setTranslation(position)
    }
}
