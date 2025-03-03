import CryptoJS from "crypto-js";
const key2 = "trMrHtvjo6myautxDUiAcYsVtaeQ8nhf";

// amount=71000&appid=2554&apptransid=241129_5719004757&bankcode=&
//checksum=ccaba75bed7ec9fcd84994dd6b637f806b75a3820383ba23e9032eae02bd099b&discountamount=0&pmcid=38&status=1

const param = {
  amount: 71000,
  appid: 2554,
  apptransid: "241129_5719004757",
  bankcode: "",
  discountamount: 0,
  pmcid: 38,
  status: 1,
};

let checksumData =
  param.appid +
  "|" +
  param.apptransid +
  "|" +
  param.pmcid +
  "|" +
  param.bankcode +
  "|" +
  param.amount +
  "|" +
  param.discountamount +
  "|" +
  param.status;
console.log(checksumData);

const mac = CryptoJS.HmacSHA256(checksumData, key2).toString();

export { mac };
