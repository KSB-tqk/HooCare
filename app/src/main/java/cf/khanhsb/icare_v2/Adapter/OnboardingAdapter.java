package cf.khanhsb.icare_v2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cf.khanhsb.icare_v2.Model.OnboardingItem;
import cf.khanhsb.icare_v2.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new OnboardingViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_onboarding,parent,false)
    );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
    holder.setOnboardingData(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageOnboarding;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageOnboarding = imageOnboarding;
            imageOnboarding = itemView.findViewById(R.id.imageOnboarding);
        }


        void setOnboardingData(OnboardingItem onboardingItem)
        {
            imageOnboarding.setImageResource(onboardingItem.getImage());
        }
    }
}
