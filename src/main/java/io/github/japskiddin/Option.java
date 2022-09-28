package io.github.japskiddin;

/**
 * Convenient "-flag opt" combination
 */
class Option {
  private final String flag, opt;

  /**
   * @param flag Option's key
   * @param opt Option's value
   */
  public Option(String flag, String opt) {
    this.flag = flag;
    this.opt = opt;
  }

  public String getOpt() {
    return opt;
  }

  public String getFlag() {
    return flag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Option option = (Option) o;
    return opt.equals(option.getOpt());
  }

  @Override
  public int hashCode() {
    return opt.hashCode() + flag.hashCode();
  }
}