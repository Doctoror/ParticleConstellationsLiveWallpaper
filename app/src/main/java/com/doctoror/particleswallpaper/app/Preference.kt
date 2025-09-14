package com.doctoror.particleswallpaper.app

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.doctoror.particleswallpaper.R

open class Preference @JvmOverloads constructor(
    val context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) {

    var isEnabled = true

    var layoutResId: Int = R.layout.preference

    var widgetLayoutResId = 0

    var title: CharSequence? = null

    var summary: CharSequence? = null

    var onPreferenceChangeListener: OnPreferenceChangeListener? = null

    private var value: Any? = null

    fun callChangeListener(newValue: Any?): Boolean {
        return onPreferenceChangeListener == null ||
                onPreferenceChangeListener!!.onPreferenceChange(this, newValue)
    }

    fun notifyChanged() {
        onPreferenceChangeListener?.onPreferenceChange(this, value)
    }

    fun getPersisted(defaultValue: Any?): Any? = value ?: defaultValue

    fun persist(value: Any) {
        this.value = value
    }

    open fun onClick() {

    }

    /**
     * Gets the View that will be shown in the [PreferenceActivity].
     *
     * @param convertView The old View to reuse, if possible. Note: You should
     * check that this View is non-null and of an appropriate type
     * before using. If it is not possible to convert this View to
     * display the correct data, this method can create a new View.
     * @param parent The parent that this View will eventually be attached to.
     * @return Returns the same Preference object, for chaining multiple calls
     * into a single statement.
     * @see .onCreateView
     * @see .onBindView
     */
    fun getView(convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = onCreateView(parent)
        }
        onBindView(convertView)
        return convertView
    }

    /**
     * Creates the View to be shown for this Preference in the
     * [PreferenceActivity]. The default behavior is to inflate the main
     * layout of this Preference (see [.setLayoutResource]. If
     * changing this behavior, please specify a [ViewGroup] with ID
     * [android.R.id.widget_frame].
     *
     *
     * Make sure to call through to the superclass's implementation.
     *
     * @param parent The parent that this View will eventually be attached to.
     * @return The View that displays this Preference.
     * @see .onBindView
     */
    protected fun onCreateView(parent: ViewGroup?): View {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val layout: View = layoutInflater.inflate(layoutResId, parent, false)

        val widgetFrame = layout
            .findViewById<View?>(android.R.id.widget_frame) as ViewGroup?
        if (widgetFrame != null) {
            if (widgetLayoutResId != 0) {
                layoutInflater.inflate(widgetLayoutResId, widgetFrame)
            } else {
                widgetFrame.setVisibility(View.GONE)
            }
        }
        return layout
    }

    /**
     * Binds the created View to the data for this Preference.
     *
     *
     * This is a good place to grab references to custom Views in the layout and
     * set properties on them.
     *
     *
     * Make sure to call through to the superclass's implementation.
     *
     * @param view The View that shows this Preference.
     * @see .onCreateView
     */
    protected open fun onBindView(view: View) {
        val titleView = view.findViewById<View?>(android.R.id.title) as TextView?
        if (titleView != null) {
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title)
                titleView.setVisibility(View.VISIBLE)
            } else {
                titleView.setVisibility(View.GONE)
            }
        }

        val summaryView = view.findViewById<View?>(android.R.id.summary) as TextView?
        if (summaryView != null) {
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary)
                summaryView.setVisibility(View.VISIBLE)
            } else {
                summaryView.setVisibility(View.GONE)
            }
        }

        view.isEnabled = isEnabled

    }

    fun interface OnPreferenceChangeListener {
        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param preference The changed Preference.
         * @param newValue The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean
    }
}