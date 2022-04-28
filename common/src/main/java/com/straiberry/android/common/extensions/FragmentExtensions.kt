package com.straiberry.android.common.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun FragmentActivity.replaceFragment(fragment: Fragment, @IdRes container: Int, addToBackStack: Boolean = false) {
    supportFragmentManager.beginTransaction().apply {
        if (addToBackStack) {
            addToBackStack(fragment.tag)
        }
    }
            .replace(container, fragment)
            .commitAllowingStateLoss()
}

fun FragmentActivity.goBack() {
    supportFragmentManager.popBackStack()
}

fun showBottomSheet(
        bottomSheetDialogFragment: BottomSheetDialogFragment,
        fragmentManager: FragmentManager,
        tag: String
){
    bottomSheetDialogFragment.show(fragmentManager,tag)
}

fun BottomSheetDialogFragment.showBottomSheet(fragmentManager: FragmentManager){
    this.show(fragmentManager,this.tag)
}