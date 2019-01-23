import About from "@/components/About";
import DealsTable from "@/components/common/DealsTable";
import MyServices from "@/components/MyServices";
import Register from "@/components/Register";
import Wallet from "@/components/Wallet";
import Services from "@/components/Services";
import Service from "@/components/Service";
import User from "@/components/User";
import Profile from "@/components/user/Profile";
import NewDeal from "@/components/NewDeal";
import Deal from "@/components/Deal";

import MainPageComp from "@/components/MainPageComp";
import OurTeamPage from "@/components/OurTeamPage";
import AboutUsPage from "@/components/fromhostespage/AboutUsPage";
import RulesAndPolicyPage from "@/components/fromhostespage/RulesAndPolicyPage";
import FAQPage from "@/components/fromhostespage/FAQPage";
import ContactsPage from "@/components/fromhostespage/ContactsPage";

export const clientRouter = [
  {path: '/contactspage', component: ContactsPage,meta: {nonRequiresAuth:true}},
  {path: '/faqpage', component: FAQPage,meta: {nonRequiresAuth:true}},
  {path: '/rulespolicypage', component: RulesAndPolicyPage,meta: {nonRequiresAuth:true}},
  {path: '/aboutuspage', component: AboutUsPage,meta: {nonRequiresAuth:true}},
  {path: '/ourteampage', component: OurTeamPage,meta: {nonRequiresAuth:true}},
  {path: '/', component: MainPageComp,meta: {nonRequiresAuth:true}},
  //{path: '/', component: Services,meta: {landing: true,nonRequiresAuth:true}},
  {path: '/register', meta: {loginPage: true, nonRequiresAuth: true}, component: Register},
  {path: '/myservices', component: MyServices,meta:{role:"ROLE_USER"}, redirect: 'myservices/3'},
  {path: '/myservices/:status', component: MyServices,meta:{role:"ROLE_USER"}},
  {path: '/mydeals/:get/:type', component: DealsTable,meta:{role:"ROLE_USER"}},
  {path: '/mydeals', redirect: '/mydeals/servicesido/active'},

  {path: '/deals/:id', component: Deal,meta:{role:["ROLE_USER","ROLE_MAIN_ADMIN", "ROLE_ADMIN","ROLE_CURATOR","ROLE_MAIN_CURATOR"]}},
  {path: '/wallet', component: Wallet,meta:{role:"ROLE_USER"}},
  {path: '/profile', component: Profile},
  {path: '/services', component: Services,meta: {nonRequiresAuth:true}},
  {path: '/services/:category', component: Services,meta: {nonRequiresAuth:true}},
  {path: '/services/:category/:search', component: Services,meta: {nonRequiresAuth:true}},
  {path: '/service/:id', component: Service},
  {path: '/service/:id/reviews', component: Service, props: {activeTabParam:2}},
  {path: '/service/:id/serviceinfo', component: Service, props: {activeTabParam:0}},
  {path: '/deals/new/:serviceid', component: NewDeal},
  {path: '/user/:id', component: User},
  {path: '/about', component: About},


];
