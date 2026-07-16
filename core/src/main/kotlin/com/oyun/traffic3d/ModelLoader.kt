package com.oyun.traffic3d

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import net.mgsx.gltf.loaders.glb.GLBLoader
import net.mgsx.gltf.loaders.gltf.GLTFLoader

/**
 * assets/models/ altına indirdiğin .glb ya da .gltf araba modelini buradan yükleriz.
 * Dosya henüz yoksa (test aşamasındasın) kırmızı bir kutu placeholder olarak
 * kullanılır, böylece model eklemeden de oyunu test edebilirsin.
 *
 * Model eklerken: dosyayı core/src/main/assets/models/car1.glb konumuna koy,
 * git add + commit + push yap, GitHub Actions APK'yı bu modelle derleyecek.
 */
object ModelLoader {

    fun loadCarOrPlaceholder(assetPath: String): ModelInstance {
        val file = Gdx.files.internal(assetPath)

        if (file.exists()) {
            return try {
                val sceneAsset = if (assetPath.endsWith(".glb")) {
                    GLBLoader().load(file)
                } else {
                    GLTFLoader().load(file)
                }
                ModelInstance(sceneAsset.scene.model)
            } catch (e: Exception) {
                Gdx.app.error("ModelLoader", "Model yuklenemedi: $assetPath, placeholder kullaniliyor", e)
                placeholderBox()
            }
        }

        return placeholderBox()
    }

    private fun placeholderBox(): ModelInstance {
        val builder = ModelBuilder()
        val model = builder.createBox(
            1.8f, 1.2f, 4.2f,
            Material(ColorAttribute.createDiffuse(Color.RED)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        return ModelInstance(model)
    }
}
