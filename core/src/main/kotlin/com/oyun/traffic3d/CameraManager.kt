package com.oyun.traffic3d

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3

enum class CameraMode { CHASE, HOOD, INTERIOR }

/**
 * Traffic Racer'daki gibi 3 kamera modu arasında geçiş yapar.
 *
 * ÖNEMLİ: Aşağıdaki offset değerleri (özellikle interiorOffset) senin gerçek
 * araç modelinin ölçeğine göre AYARLANMALI. Model Blender'da 1 birim = 1 metre
 * olacak şekilde export edilmişse bu değerler yaklaşık doğru olur; değilse
 * kamera koltuğun altından ya da direksiyonun içinden bakabilir, sayıları
 * deneme-yanılmayla düzelt.
 */
class CameraManager(private val camera: PerspectiveCamera, private val car: CarController) {

    private var mode = CameraMode.CHASE

    private val chaseOffset = Vector3(0f, 2.5f, -6.5f)
    private val hoodOffset = Vector3(0f, 1.15f, -0.3f)
    private val interiorOffset = Vector3(-0.35f, 0.95f, 0.15f) // sürücü koltuğu / direksiyon arkası

    fun nextMode() {
        mode = when (mode) {
            CameraMode.CHASE -> CameraMode.HOOD
            CameraMode.HOOD -> CameraMode.INTERIOR
            CameraMode.INTERIOR -> CameraMode.CHASE
        }
    }

    fun setMode(newMode: CameraMode) {
        mode = newMode
    }

    fun update() {
        val offset = when (mode) {
            CameraMode.CHASE -> chaseOffset
            CameraMode.HOOD -> hoodOffset
            CameraMode.INTERIOR -> interiorOffset
        }

        val rotatedOffset = Vector3(offset).rotateRad(Vector3.Y, car.rotationY)
        camera.position.set(car.position).add(rotatedOffset)

        val lookAhead = if (mode == CameraMode.INTERIOR) 8f else 15f
        val lookTarget = Vector3(0f, offset.y * 0.6f, lookAhead).rotateRad(Vector3.Y, car.rotationY)
        camera.lookAt(Vector3(car.position).add(lookTarget))

        camera.up.set(Vector3.Y)
        camera.update()
    }
}
