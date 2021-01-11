import datetime
import gzip
import random
# from province_city import language_code
import string
from datetime import timedelta
from multiprocessing import Process
from pathlib import Path

from faker import Faker
from faker.providers import address
from faker.providers import date_time
from faker.providers import python

# pip install faker==4.0.1
# https://faker.readthedocs.io/en/master/fakerclass.html
#
# Sample:
#   {"Orders": [
#         {
#           "AmazonOrderId": "902-3159896-1390916",
#           "PurchaseDate": "2017-01-20T19:49:35Z",
#           "LastUpdateDate": "2017-01-20T19:49:35Z",
#           "OrderStatus": "Pending",
#           "FulfillmentChannel": "SellerFulfilled",
#           "NumberOfItemsShipped": 0,
#           "NumberOfItemsUnshipped": 0,
#           "PaymentMethod": "Other",
#           "PaymentMethodDetails": [
#             "CreditCard",
#             "GiftCerificate"
#           ],
#           "MarketplaceId": "ATVPDKIKX0DER",
#           "ShipmentServiceLevelCategory": "Standard",
#           "OrderType": "StandardOrder",
#           "EarliestShipDate": "2017-01-20T19:51:16Z",
#           "LatestShipDate": "2017-01-25T19:49:35Z",
#           "IsBusinessOrder": false,
#           "IsPrime": false,
#           "IsGlobalExpressEnabled": false,
#           "IsPremiumOrder": false,
#           "IsSoldByAB": false
#         }
#       ]}


file_dir = "fakeData/"
separtor = '|'
letters = string.ascii_letters

row = '"SellerId": "{}", ' \
      '"AmazonOrderId": "{}", ' \
      '"PurchaseDate": "{}", ' \
      '"LastUpdateDate": "{}", ' \
      '"OrderStatus": "{}", ' \
      '"FulfillmentChannel": "{}", ' \
      '"NumberOfItemsShipped": {}, ' \
      '"NumberOfItemsUnshipped": {}, ' \
      '"PaymentMethod": "Other", ' \
      '"PaymentMethodDetails": [ ' \
      '"{}", ' \
      '"{}" ' \
      '], ' \
      '"MarketplaceId": "{}", ' \
      '"ShipmentServiceLevelCategory": "{}", ' \
      '"OrderType": "StandardOrder", ' \
      '"EarliestShipDate": "{}", ' \
      '"LatestShipDate": "{}", ' \
      '"IsBusinessOrder": false, ' \
      '"IsPrime": {}, ' \
      '"IsGlobalExpressEnabled": {}, ' \
      '"IsPremiumOrder": {}, ' \
      '"IsSoldByAB": {} '

f = Faker()
f.add_provider(python)
f.add_provider(date_time)
f.add_provider(address)
Faker.seed(6789)

Sellers = ("SellerA", "SellerB", "SellerC")
app_id_elements = tuple("fancy_game_" + str(t) for t in range(99))
other_type = tuple("other_" + str(t) for t in range(95))
types = ("login", "tutorial", "new_user", "payment") + other_type
m = 1000000
k = 1000
s = 128

OrderStatus = ("Pending", "Paid", "Shipping")
FulfillmentChannel = ("SellerFulfilled", "FBA")
PaymentMethod = ("Discount", "GiftCerificate", "CreditCard")
MarketplaceId = ("ATVPDKIKX0DER", "LASKDFUIYAUY", "ASHDFKJHWENRB")
ShipmentServiceLevelCategory = ("Standard", "1-day", "15-day")
OrderType = ("StandardOrder")

#  --------- ref ------------

language_code = ["af-ZA", "am-ET", "ar-AE", "ar-BH", "ar-DZ", "ar-EG", "ar-IQ", "ar-JO", "ar-KW", "ar-LB", "ar-LY",
                 "ar-MA", "arn-CL", "ar-OM", "ar-QA", "ar-SA", "ar-SY", "ar-TN", "ar-YE", "as-IN", "az-Cyrl-AZ",
                 "az-Latn-AZ", "ba-RU", "be-BY", "bg-BG", "bn-BD", "bn-IN", "bo-CN", "br-FR", "bs-Cyrl-BA",
                 "bs-Latn-BA",
                 "ca-ES", "co-FR", "cs-CZ", "cy-GB", "da-DK", "de-AT", "de-CH", "de-DE", "de-LI", "de-LU", "dsb-DE",
                 "dv-MV", "el-GR", "en-029", "en-AU", "en-BZ", "en-CA", "en-GB", "en-IE", "en-IN", "en-JM", "en-MY",
                 "en-NZ", "en-PH", "en-SG", "en-TT", "en-US", "en-ZA", "en-ZW", "es-AR", "es-BO", "es-CL", "es-CO",
                 "es-CR", "es-DO", "es-EC", "es-ES", "es-GT", "es-HN", "es-MX", "es-NI", "es-PA", "es-PE", "es-PR",
                 "es-PY", "es-SV", "es-US", "es-UY", "es-VE", "et-EE", "eu-ES", "fa-IR", "fi-FI", "fil-PH", "fo-FO",
                 "fr-BE", "fr-CA", "fr-CH", "fr-FR", "fr-LU", "fr-MC", "fy-NL", "ga-IE", "gd-GB", "gl-ES", "gsw-FR",
                 "gu-IN", "ha-Latn-NG", "he-IL", "hi - IN", "hr-BA", "hr-HR", "hsb-DE", "hu-HU", "hy-AM", "id-ID",
                 "ig-NG", "ii-CN", "is-IS", "it-CH", "it-IT", "iu-Cans-CA", "iu-Latn-CA", "ja-JP", "ka-GE", "kk-KZ",
                 "kl-GL", "km-KH", "kn-IN", "kok-IN", "ko-KR", "ky-KG", "lb-LU", "lo-LA", "lt-LT", "lv-LV", "mi-NZ",
                 "mk-MK", "ml-IN", "mn-MN", "mn-Mong-CN", "moh-CA", "mr-IN", "ms-BN", "ms-MY", "mt-MT", "nb-NO",
                 "ne-NP",
                 "nl-BE", "nl-NL", "nn-NO", "nso-ZA", "oc-FR", "or-IN", "pa-IN", "pl-PL", "prs-AF", "ps-AF", "pt-BR",
                 "pt-PT", "qut-GT", "quz-BO", "quz-EC", "quz-PE", "rm-CH", "ro-RO", "ru-RU", "rw-RW", "sah-RU", "sa-IN",
                 "se-FI", "se-NO", "se-SE", "si-LK", "sk-SK", "sl-SI", "sma-NO", "sma-SE", "smj-NO", "smj-SE", "smn-FI",
                 "sms-FI", "sq-AL", "sr-Cyrl-BA", "sr-Cyrl-CS", "sr-Cyrl-ME", "sr-Cyrl-RS", "sr-Latn-BA", "sr-Latn-CS",
                 "sr-Latn-ME", "sr-Latn-RS", "sv-FI", "sv-SE", "sw-KE", "syr-SY", "ta-IN", "te-IN", "tg-Cyrl-TJ",
                 "th-TH", "tk-TM", "tn-ZA", "tr-TR", "tt-RU", "tzm-Latn-DZ", "ug-CN", "uk-UA", "ur-PK", "uz-Cyrl-UZ",
                 "uz-Latn-UZ", "vi-VN", "wo-SN", "xh-ZA", "yo-NG", "zh-CN", "zh-HK", "zh-MO", "zh-SG", "zh-TW", "zu-ZA"]


def get_true_false():
    if random.randint(0, 9) >= 5:
        return "true"
    else:
        return "false"


def get_random_date(from_y, from_m, from_d, to_y, to_m, to_d):
    try:
        date = f.date_time_between(start_date=datetime.datetime(from_y, from_m, from_d, 0, 0),
                                   end_date=datetime.datetime(to_y, to_m, to_d, 0, 0),
                                   tzinfo=None)
        # Thu, 01 Sep 2016 10:11:12 +0000 -- rfc2822
        # YYYY/MM/DD-HH:MM:SS.UUUU
        # return date.astimezone().strftime("%a, %d %b %Y %X %z")
        # return date.strftime("%Y/%m/%d-%X.0000")
        # 2021-01-08 20:23:47 https://docs.aws.amazon.com/athena/latest/ug/create-table.html
        return date.astimezone().strftime("%Y-%m-%d %X")
        # return date.astimezone().isoformat()
    except:
        print(">>>>>> Skip Day {}-{}".format(to_m, to_d))
        return ""


def generate_one_json_row(from_y, from_m, from_d, to_y, to_m, to_d):
    data_range = (from_y, from_m, from_d, to_y, to_m, to_d)

    r = row.format(
        f.random_element(Sellers),
        str(random.randint(1, 999)).zfill(3) + "-" + str(random.randint(1, m * 9)).zfill(7) + "-" + str(
            random.randint(1, m * 9)).zfill(7),
        get_random_date(*data_range),  # PurchaseDate
        get_random_date(*data_range),
        f.random_element(OrderStatus),
        f.random_element(FulfillmentChannel),
        random.randint(1, 5),  # Item shipped
        random.randint(0, 1),  # Item not shipped
        f.random_element(PaymentMethod),
        f.random_element(PaymentMethod),  # May duplicate
        f.random_element(MarketplaceId),
        f.random_element(ShipmentServiceLevelCategory),
        get_random_date(*data_range),
        get_random_date(*data_range),
        get_true_false(),  # is prime
        get_true_false(),
        get_true_false(),
        get_true_false(),
        # f.country(),
    )

    return "{" + r + "}" + "\n"


def generate_path(from_y, from_m, from_d, to_y, to_m, to_d):
    """
    s3://<bucket_name>/adjust/raw_events/date=2020-02-20/raw_events_00001.gz
    s3://<bucket_name>/adjust/raw_events/date=2020-02-20/raw_events_00001.parquet
    """
    # generate path
    f_d = datetime.datetime(from_y, from_m, from_d)
    t_d = datetime.datetime(to_y, to_m, to_d)
    delta = t_d - f_d
    print("time delta in days- " + str(delta.days))
    all_path = []
    for d in range(delta.days):
        log_date = f_d + timedelta(days=d)
        partion_date = "date=" + log_date.strftime("%Y-%m-%d")
        # print(partion_date)
        per_hour_file_names = [str(n).zfill(5) for n in range(24)]  # one day will have 240 files
        for hour in per_hour_file_names:
            raw_path = "fakeData/orders/raw_events/{}/".format(partion_date)
            raw_file_name = "raw_events_{}.json".format(hour)
            # Make sure the paths are created.
            Path(raw_path).mkdir(parents=True, exist_ok=True)
            all_path.append((raw_path + raw_file_name, log_date))

    return all_path


def process_one_piece(p, a_slice):
    """
    :param a_slice: A list of (path: date)
    :return:
    """
    # a_slice = a_slice[0]
    print("================== a process is started to process {} files ==============".format(len(a_slice)))
    for i in a_slice:

        # with gzip.open(i[0], "a", 9) as file:
        with open(i[0], "w") as file:
            print("process " + str(p) + ", write file: " + i[0] + ", for date " + str(i[1].day))
            d = i[1]
            for r in range(ROWS_A_FILE):
                file.write(str(generate_one_json_row(d.year, d.month, d.day, d.year, d.month, d.day + 1)))
    print("completed one.")


def process_one_piece_gz(p, a_slice):
    """
    :param a_slice: A list of (path: date)
    :return:
    """
    # a_slice = a_slice[0]
    print("================== a process is started to process {} files ==============".format(len(a_slice)))
    for i in a_slice:

        with gzip.open(i[0], "a", 9) as file:
            print("process " + str(p) + ", write file: " + i[0] + ", for date " + str(i[1].day))
            d = i[1]
            for r in range(ROWS_A_FILE):
                file.write(generate_one_json_row(d.year, d.month, d.day, d.year, d.month, d.day + 1).encode("utf-8"))
    print("completed one.")


ROWS_A_FILE = s  # s=128 k = 117KB, m = 116M, if set at m, then 1 day is 240*116M = 28G, 1 year is 10T

if __name__ == "__main__":
    """
    == How to use ==

    If set ROWS_A_FILE as m, then 1 GZ file is 116M, 1 day is 240*116M = 28G, 1 year is 10T

    """
    # Time span will decide how much log will be generated.
    all_path = generate_path(2019, 1, 1, 2019, 1, 3)
    # How many process to use.
    num_process = 8
    batch_size = len(all_path) // num_process
    remain_size = len(all_path) % num_process
    print("remain size " + str(remain_size))
    crr = 0
    for p in range(num_process):
        a_slice = all_path[crr:crr + batch_size]
        print("slice size " + str((crr, crr + batch_size)))
        crr = crr + batch_size
        ps = Process(target=process_one_piece, args=[p, a_slice])
        ps.start()
    if remain_size > 0:
        Process(target=process_one_piece, args=[p, all_path[-remain_size]]).start()
