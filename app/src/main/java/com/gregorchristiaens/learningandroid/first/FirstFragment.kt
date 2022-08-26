package com.gregorchristiaens.learningandroid.first

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gregorchristiaens.learningandroid.R
import com.gregorchristiaens.learningandroid.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    /**
     * The [FragmentFirstBinding] is stored as a property.
     * We can't initially request the binding, we do this in [onCreateView] because we need the inflater and container to do so.
     * We create one nullable variable [_binding] that will be used to set the value of our binding.
     * We also create a safe non-nullable value [binding] that will be used to request the value of our binding.
     * The !! in the get method of [binding] casts our FragmentBinding? to FragmentBinding, it will throw a NullPointerException if the value is null
     * We don't provide a setter for [binding] because this value is only used to get a safe non-nullable variant of our FragmentBinding.
     * [_binding] serves as a Backing Property for [binding] we can call [_binding] in the get method of [binding] without initializing a backing field.
     *
     * @see [backing field](https://kotlinlang.org/docs/properties.html#backing-fields)
     * @see [stack-overflow _binding & binding](https://stackoverflow.com/questions/63189584/why-in-android-binding-examples-google-uses-val-binding-and-var-binding
     * @see [stack-overflow _binding & binding](https://stackoverflow.com/questions/68124726/what-is-the-difference-between-binding-and-binding)
     *
     * The built in onCreateView Method allows for null because in some case you could have a fragment without a ui.
     * In our case however returning null when a view is expected would cause our application to crash.
     * Hence we avoid returning null by having our safe non-nullable value.
     * If the fragment is in fact destroyed we will throw a NullPointerException.
     *
     * Because both binding and _binding ara private this very closely resembles the late init var property.
     * But the difference here is that we can set he value of our backing field back to null.
     * A practice that is common in [onDestroyView] to avoid memory leaks.
     *
     * A fragment could outlive it view hence this patterns is applied only in fragments.
     *
     * One of the downsides of this pattern is that you have to be careful not to call
     * binding after the fragment is destroyed.
     *
     * This property is only valid between onCreateView and onDestroyView.
     **/
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}