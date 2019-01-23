export const actions = {

  setCommonData ({commit}, payload) {
    let data = {};
    data.univers= payload.univers;

    let categories =[];
    payload.categories.forEach((c)=>
      {

        categories.push({header:true,item:c});
        c.categories.forEach((c)=> categories.push(c));
      }
    );
    data.categories=categories;
    commit ('setCommonData', data);
  },
  setCommonData ({commit}, payload) {

    commit ('setCommonData', payload);
  }
};
