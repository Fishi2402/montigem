(element) => {
  const style = window.getComputedStyle(element);
  return {
    top: style.paddingTop,
    right: style.paddingRight,
    bottom: style.paddingBottom,
    left: style.paddingLeft
  };
}
