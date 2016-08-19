
package anton46.sample.timepicker;

import java.util.List;

public interface WheelAdapter{
    int getItemsCount();

    String getItem(int index);

    int getMaximumLength();

    void bindData(List datas);
}
