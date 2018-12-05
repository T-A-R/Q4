package pro.quizer.quizerexit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import pro.quizer.quizerexit.model.config.ElementModel;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBind(final ElementModel pAnswer, final int pPosition);
}
