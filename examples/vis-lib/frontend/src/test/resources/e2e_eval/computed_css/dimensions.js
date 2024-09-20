(element) => {
  const style = window.getComputedStyle(element);
  return {
    width: style.width,
    height: style.height
  };
}
