(element) => {
  const style = window.getComputedStyle(element);
  return {
    top: style.marginTop,
    right: style.marginRight,
    bottom: style.marginBottom,
    left: style.marginLeft
  };
}
