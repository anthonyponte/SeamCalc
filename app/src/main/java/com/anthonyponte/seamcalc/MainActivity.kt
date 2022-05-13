package com.anthonyponte.seamcalc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProviders
import com.anthonyponte.seamcalc.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MeasuresViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        val colorOnBackground = MaterialColors.getColor(root, R.attr.colorOnBackground)
        val colorError = MaterialColors.getColor(root, R.attr.colorError)
        val colorSucces = MaterialColors.getColor(root, R.attr.colorSucces)
        val colorWarning = MaterialColors.getColor(root, R.attr.colorWarning)

        binding.etEspesorCuerpo.addTextChangedListener(watcher)
        binding.etEspesorTapa.addTextChangedListener(watcher)
        binding.etGanchoCuerpo.addTextChangedListener(watcher)
        binding.etGanchoTapa.addTextChangedListener(watcher)
        binding.etAlturaCierre.addTextChangedListener(watcher)
        binding.etEspesorCierre.addTextChangedListener(watcher)

        model = ViewModelProviders.of(this).get(MeasuresViewModel::class.java)

        model.measures.observe(this, { item ->
            binding.tvTraslape.text = format(item.traslape)
            binding.tvSuperposicion.text = format(item.superposicion)
            binding.tvPenetracion.text = format(item.penetracion)
            binding.tvEspacioLibre.text = format(item.espacioLibre)
            binding.tvCompacidad.text = format(item.compacidad)

            if (item.traslape >= 1) {
                binding.tvTraslape.setTextColor(colorSucces)
            } else if (item.traslape < 1 && item.traslape != 0.0) {
                binding.tvTraslape.setTextColor(colorError)
            } else if (item.traslape == 0.0) {
                binding.tvTraslape.setTextColor(colorOnBackground)
            }

            if (item.superposicion >= 80) {
                binding.tvSuperposicion.setTextColor(colorSucces)
            } else if (item.superposicion < 80 && item.superposicion >= 45 && item.superposicion!= 0.0) {
                binding.tvSuperposicion.setTextColor(colorWarning)
            }else if (item.superposicion < 45 && item.superposicion!= 0.0) {
                binding.tvSuperposicion.setTextColor(colorError)
            } else if (item.superposicion == 0.0) {
                binding.tvSuperposicion.setTextColor(colorOnBackground)
            }

            if (item.penetracion >= 95) {
                binding.tvPenetracion.setTextColor(colorSucces)
            } else if (item.penetracion < 95 && item.penetracion > 70) {
                binding.tvPenetracion.setTextColor(colorWarning)
            } else if (item.penetracion <= 70 && item.penetracion != 0.0) {
                binding.tvPenetracion.setTextColor(colorError)
            } else if (item.penetracion == 0.0) {
                binding.tvPenetracion.setTextColor(colorOnBackground)
            }

            if (item.espacioLibre > 0.19) {
                binding.tvEspacioLibre.setTextColor(colorError)
            } else if (item.espacioLibre <= 0.19 && item.espacioLibre != 0.0) {
                binding.tvEspacioLibre.setTextColor(colorSucces)
            } else if (item.espacioLibre == 0.0)  {
                binding.tvEspacioLibre.setTextColor(colorOnBackground)
            }

            if (item.compacidad >= 85) {
                binding.tvCompacidad.setTextColor(colorSucces)
            } else if (item.compacidad < 85 && item.compacidad >= 75) {
                binding.tvCompacidad.setTextColor(colorWarning)
            } else if (item.compacidad < 75 && item.compacidad >= 1) {
                binding.tvCompacidad.setTextColor(colorError)
            } else if (item.compacidad == 0.0) {
                binding.tvCompacidad.setTextColor(colorOnBackground)
            }
        })

        binding.btnLimpiar.setOnClickListener {
            val measure = Measures()
            measure.traslape = 0.0
            measure.compacidad = 0.0
            measure.penetracion = 0.0
            measure.superposicion = 0.0
            measure.espacioLibre = 0.0

            model.setMeasure(measure)

            binding.etEspesorCuerpo.text?.clear()
            binding.etEspesorTapa.text?.clear()
            binding.etGanchoCuerpo.text?.clear()
            binding.etGanchoTapa.text?.clear()
            binding.etAlturaCierre.text?.clear()
            binding.etEspesorCierre.text?.clear()

            binding.etEspesorCuerpo.requestFocusFromTouch()
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            binding.btnLimpiar.isEnabled = isNotEmpty()

            if (isNotEmpty()) {
                val espesorCuerpo = binding.etEspesorCuerpo.text.toString().toDouble()
                val espesorTapa = binding.etEspesorTapa.text.toString().toDouble()
                val ganchoCuerpo = binding.etGanchoCuerpo.text.toString().toDouble()
                val ganchoTapa = binding.etGanchoTapa.text.toString().toDouble()
                val alturaCierre = binding.etAlturaCierre.text.toString().toDouble()
                val espesorCierre = binding.etEspesorCierre.text.toString().toDouble()

                val measures = Measures()

                measures.traslape = traslape(ganchoTapa, ganchoCuerpo, espesorTapa, alturaCierre)

                measures.superposicion =
                    superposicion(
                        ganchoTapa,
                        ganchoCuerpo,
                        espesorTapa,
                        alturaCierre,
                        espesorCuerpo
                    )

                measures.penetracion =
                    penetracion(ganchoCuerpo, espesorCuerpo, alturaCierre, espesorTapa)

                measures.espacioLibre = espacioLibre(espesorCierre, espesorTapa, espesorCuerpo)

                measures.compacidad = compacidad(espesorCuerpo, espesorTapa, espesorCierre)

                model.setMeasure(measures)
            } else {
                val measures = Measures()
                measures.traslape = 0.0
                measures.compacidad = 0.0
                measures.penetracion = 0.0
                measures.superposicion = 0.0
                measures.espacioLibre = 0.0

                model.setMeasure(measures)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    fun isNotEmpty(): Boolean {
        return binding.etEspesorCuerpo.text.toString().isNotEmpty() &&
                binding.etEspesorTapa.text.toString().isNotEmpty() &&
                binding.etGanchoCuerpo.text.toString().isNotEmpty() &&
                binding.etGanchoTapa.text.toString().isNotEmpty() &&
                binding.etAlturaCierre.text.toString().isNotEmpty() &&
                binding.etEspesorCierre.text.toString().isNotEmpty()
    }

    private fun traslape(
        ganchoTapa: Double,
        ganchoCuerpo: Double,
        espesorTapa: Double,
        alturaCierre: Double
    ): Double {
        return ganchoTapa + ganchoCuerpo + 1.1 * espesorTapa - alturaCierre
    }

    private fun superposicion(
        ganchoTapa: Double,
        ganchoCuerpo: Double,
        espesorTapa: Double,
        alturaCierre: Double,
        espesorCuerpo: Double
    ): Double {
        return ((ganchoTapa + ganchoCuerpo + 1.1 * espesorTapa - alturaCierre) / (alturaCierre - 1.1 * (2 * espesorTapa + espesorCuerpo))) * 100
    }

    private fun penetracion(
        ganchoCuerpo: Double,
        espesorCuerpo: Double,
        alturaCierre: Double,
        espesorTapa: Double
    ): Double {
        return ((ganchoCuerpo - (1.1 * espesorCuerpo)) * 100) / (alturaCierre - 1.1 * ((2 * espesorTapa) + espesorCuerpo))
    }

    private fun espacioLibre(
        espesorCierre: Double,
        espesorTapa: Double,
        espesorCuerpo: Double
    ): Double {
        return espesorCierre - (3 * espesorTapa + 2 * espesorCuerpo)
    }

    private fun compacidad(
        espesorCuerpo: Double,
        espesorTapa: Double,
        espesorCierre: Double
    ): Double {
        return ((2 * espesorCuerpo + 3 * espesorTapa) / espesorCierre) * 100
    }

    private fun format(num: Double): String {
        val df = DecimalFormat("#.###")
        return df.format(num)
    }
}