Handlebars.registerHelper("firstWord", function(options) {
  return options.fn(this).split(" ")[0];
});