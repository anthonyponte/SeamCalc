package com.anthonyponte.seamcalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MeasuresViewModel : ViewModel() {
    private val _measures = MutableLiveData<Measures>()
    val measures: LiveData<Measures> get() = _measures

    fun setMeasure(value: Measures) {
        _measures.postValue(value)
    }
}