from faker import Faker
import random
import datetime
import hashlib
# from province_city import language_code
from multiprocessing import Process
import string
from faker.providers import python
from faker.providers import date_time
from faker.providers import address
from pathlib import Path
from datetime import timedelta
from multiprocessing import Process
import gzip

file_dir = "fakeData/"
separtor = '|'
letters = string.ascii_letters
row = '"app_id":"{}", "user_id":"{}", "channel_id":"{}", "server_id":"{}", "device_id":"{}", "role_name":"{}", ' \
      '"level":"{}", ' \
      '"vip":"{}", "trans_id":"{}", "payment_usd":"{}", "ip":"{}", "model":"{}", "resolution":"{}", "network":"{}", ' \
      '"os":"{}", ' \
      '"os_version":"{}", "provider":"{}", "event_time":"{}", "user_source":"{}", "game_version":"{}", "language":"{' \
      '}", ' \
      '"country":"{}", "event_type":"{}" '

f = Faker()
f.add_provider(python)
f.add_provider(date_time)
f.add_provider(address)
Faker.seed(6789)

app_id_elements = tuple("fancy_game_" + str(t) for t in range(99))
other_type = tuple("other_" + str(t) for t in range(95))
types = ("login", "tutorial", "new_user", "payment") + other_type
m = 1000000
k = 1000
uid_prefix = f.lexify(text='????????-????????-????????-',
                      letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
channel_id_prefix = f.lexify(text='????????-????????-????????-',
                             letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
device_id_prefix = f.lexify(text='????????-????????-????????-',
                            letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
row_name_prefix = f.lexify(text='????????-????????-????????-',
                           letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
model_prefix = f.lexify(text='????????-', letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
resolution_prefix = f.lexify(text='????????-', letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
network_prefix = f.lexify(text='????-', letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
os_elements = ("android", "ios")
provider_elements = ("China_telecom", "China_unicom", "Other_1", "Other_2", "Other_3", "other_4")
user_source_prefix = f.lexify(text='????-', letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')
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


def generate_one_json_row(from_y, from_m, from_d, to_y, to_m, to_d):
    r = row.format(
        f.random_element(app_id_elements),
        uid_prefix + str(random.randint(1, m)),
        channel_id_prefix + str(random.randint(1, k)),
        str(random.randint(1, k)),
        device_id_prefix + str(random.randint(1, k)),
        row_name_prefix + str(random.randint(1, k)),
        str(random.randint(1, k)),  # level
        str(random.randint(1, k)),  # VIP level
        f.lexify(text='????????-????????-????????-???????',  # Trans_id
                 letters='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'),
        str(random.randint(0, k)),  # payment usd
        "{}.{}.{}.{}".format(str(random.randint(0, 255)), str(random.randint(1, 255)), str(random.randint(0, 255)),
                             str(random.randint(0, 255))),
        model_prefix + str(random.randint(0, k)),  # model
        resolution_prefix + str(random.randint(0, 255)),
        network_prefix + str(random.randint(0, 64)),
        f.random_element(os_elements),
        f.random_element(os_elements) + "{}.{}.{}".format(str(random.randint(4, 15)),
                                                          str(random.randint(0, 5)),
                                                          str(random.randint(0, 5))),
        f.random_element(provider_elements),
        f.date_time_between(start_date=datetime.datetime(from_y, from_m, from_d, 0, 0),
                            end_date=datetime.datetime(to_y, to_m, to_d, 0, 0),
                            tzinfo=None),
        user_source_prefix + str(random.randint(0, 255)),
        "{}.{}.{}".format(str(random.randint(4, 15)),
                          str(random.randint(0, 5)),
                          str(random.randint(0, 5))),
        f.random_element(language_code),
        f.country(),
        f.random_element(types)
    )

    return "{" + r + "}\n"


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
        per_hour_file_names = [str(n).zfill(5) for n in range(240)]  # one day will have 240 files
        for hour in per_hour_file_names:
            raw_path = "fakeData/adjust/raw_events/{}/".format(partion_date)
            raw_file_name = "raw_events_{}.gz".format(hour)
            # Make sure the paths are created.
            Path(raw_path).mkdir(parents=True, exist_ok=True)
            all_path.append((raw_path + raw_file_name, log_date))

    return all_path


def process_one_pice(p, a_slice):
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


ROWS_A_FILE = k  # k = 117KB, m = 116M, if set at m, then 1 day is 240*116M = 28G, 1 year is 10T

if __name__ == "__main__":
    """
    == How to use ==

    If set ROWS_A_FILE as m, then 1 GZ file is 116M, 1 day is 240*116M = 28G, 1 year is 10T

    """
    # Time span will decide how much log will be generated.
    all_path = generate_path(2018, 1, 1, 2018, 3, 7)
    # How many process to use.
    num_process = 20
    batch_size = len(all_path) // num_process
    remain_size = len(all_path) % num_process
    print("remain size " + str(remain_size))
    crr = 0
    for p in range(num_process):
        a_slice = all_path[crr:crr + batch_size]
        print("slice size " + str((crr, crr + batch_size)))
        crr = crr + batch_size
        ps = Process(target=process_one_pice, args=[p, a_slice])
        ps.start()
    if remain_size > 0:
        Process(target=process_one_pice, args=[p, all_path[-remain_size]]).start()


