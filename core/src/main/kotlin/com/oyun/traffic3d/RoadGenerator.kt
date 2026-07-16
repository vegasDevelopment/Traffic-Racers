package com.oyun.traffic3d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3

/**
 * Gerçek bir yol modeli yerine şimdilik düz gri kutu segmentleri kullanıyoruz.
 * Araç ilerledikçe geride kalan segment öne taşınır - böylece sonsuz yol
 * illüzyonu oluşur, aslında sadece 6 segment var. Kendi yol/zemin modelini
 * eklemek istersen segmentModel'i ModelLoader üzerinden yüklenen bir modelle
 * değiştirebilirsin.
 */
class RoadGenerator {

    private val segmentLength = 20f
    private val segmentCount = 8

    private val modelBuilder = ModelBuilder()
    private val segmentModel: Model = modelBuilder.createBox(
        10f, 0.1f, segmentLength,
        Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
        (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
    )

    private val segments = MutableList(segmentCount) { i ->
        ModelInstance(segmentModel).apply {
            transform.setTranslation(0f, -0.6f, i * segmentLength)
        }
    }

    private val tmp = Vector3()

    fun update(carZ: Float) {
        for (segment in segments) {
            segment.transform.getTranslation(tmp)
            if (tmp.z < carZ - segmentLength * 2f) {
                val maxZ = segments.maxOf { seg ->
                    val v = Vector3()
                    seg.transform.getTranslation(v).z
                }
                segment.transform.setTranslation(0f, -0.6f, maxZ + segmentLength)
            }
        }
    }

    fun render(batch: ModelBatch, environment: Environment) {
        segments.forEach { batch.render(it, environment) }
    }

    fun dispose() {
        segmentModel.dispose()
    }
}
