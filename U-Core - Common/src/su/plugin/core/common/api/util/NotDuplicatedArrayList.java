package su.plugin.core.common.api.util;

import java.util.ArrayList;
import java.util.Collection;

public class NotDuplicatedArrayList<E> extends ArrayList<E> {

  @Override
  public boolean add(E e) {
    return contains(e) ? false : super.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    c.forEach(v -> add(v));

    return true;
  }

}
