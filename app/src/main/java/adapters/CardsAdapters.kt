package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.databinding.DialogViewGameDetailsCardItemBinding

class CardsAdapters:RecyclerView.Adapter<CardsAdapters.ViewHolder>() {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DialogViewGameDetailsCardItemBinding.inflate(LayoutInflater.from(parent.context),parent,false).root)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}