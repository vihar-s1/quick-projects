'''
- Download images from a URL.
- The images will be downloaded upto to one recursive level.
- A log is also generated to record tracebacks.
'''

import urllib.parse
from os import path, mkdir
import logging
import requests
from bs4 import BeautifulSoup


__VISITED_URLS = set()
__RECURSE = True  # flag to ensure single level recursion
__TIMEOUT = 5

__logger = logging.getLogger()
__logger.setLevel(logging.DEBUG)

__logHandler = logging.FileHandler('bulk-image-download.log')
__logHandler.setFormatter(logging.Formatter('%(asctime)s : [%(levelname)s] - %(message)s\n'))

__logger.addHandler(__logHandler)


def extract_image_name(image_url: str) -> str:
    """Extract the name of image from an URL if found.

    Args:
        image_url (str): the url to the image

    Returns:
        str: name of the image if found
    """
    image_name = image_url.split('/')[-1]

    name_ext = image_name.rsplit('.', 1)

    if name_ext[-1].lower() in ('png', 'jpg', 'jpeg', 'bmp', 'gif'):
        return '.'.join(name_ext)
    return None


def download_images(url: str, debug_statements=False) -> None:
    """Download images found by scrapping the URL "url". It returns None.

    Args:
        url (str): The URL to scrap.
        debug_statements (bool, optional):
            prints debugging statements on terminal if True. Defaults to False.
    """
    global __RECURSE

    if url in __VISITED_URLS:
        # url already visited during a recursive call so ignore the url
        return

    if debug_statements:
        print("\nScrapping following url:", url)

    # mark the url as visited for performing web scrapping operation
    __VISITED_URLS.add(url)
    try:
        # timeout of __TIMEOUT seconds if nothing found.
        response = requests.get(url, timeout=__TIMEOUT)
        response.raise_for_status() # raises exception if return code is not 200
        __logger.info('Successfuly retrieved %s', url)

    except requests.exceptions.SSLError as exception_obj:
        # in case a sites SSL verification fails for some reason
        __logger.error('SSL Error retrieving %s: %s', url, exception_obj)
        verify = input("Retry by ignoring SSL verification (y/n)?: ")

        if verify.lower() == 'y':
            response = requests.get(url, verify=False, timeout=__TIMEOUT)
            __logger.info('Succesfully retrieved %s with SSL verification ignored', url)
        else:
            return
    except requests.exceptions.RequestException as exception_obj:
        # in case that the request did not return with success code 200 or something else happened
        __logger.error('Error retrieviing %s: %s', url, exception_obj)
        return

    soup = BeautifulSoup(response.content, "html.parser")
    imgs = soup.find_all('img')

    if not path.exists('images/'):
        mkdir("images/")

    if debug_statements:
        print(f"found {len(imgs)} image-links")

    for img in imgs:
        # first checks data-src to see whether lazy-load was used or not.
        # else checks the src tag. If none are found, it returns ''
        image_url = img.get('data-src', img.get('src', ''))
        parent = img.find_parent('a')

        # try to get 'href' tag from parent if exist with the image_url as default
        if parent:
            image_url = parent.get('href', image_url)

        if not image_url:
            continue  # empty url

        # join the url to imageURL to get absolute imageURL if netloc (network location) is None
        if not urllib.parse.urlparse(image_url).netloc:
            image_url = urllib.parse.urljoin(url, image_url)

        if not urllib.parse.urlparse(image_url).scheme:
            image_url = "https:" + image_url

        try:
            image_data = requests.get(image_url, timeout=__TIMEOUT)  # getting the image data
            image_name = extract_image_name(image_url)  # extracting image name from the url

            image_data.raise_for_status()

            if not image_name:  # empty image name so not a valid image url
                continue

            with open("images/" + image_name, 'wb') as image_file:
                image_file.write(image_data.content)

            __logger.info('Successfuly retrieved image from %s', image_url)

        except requests.exceptions.RequestException as exception_obj:
            __logger.error("Error retrieving image from %s: %s", image_url, exception_obj)

    if __RECURSE:
        # performing first level recursion
        __RECURSE = False  # setting recursion flag to false to prevent further recursive calls.

        links = soup.find_all('a')

        if debug_statements:
            print(f"\nrecursing for {len(links)} links\n")

        for (i, link) in enumerate(links):
            link_url = link.get('href')

            if link_url:
                if debug_statements:
                    print(f"\n{i}. Recursing for {link_url}")

                if not urllib.parse.urlparse(link_url).netloc:
                    link_url = urllib.parse.urljoin(url, link_url)

                # Scrapping 1 level deep in case the image anchor tags contain a link to web-page
                # which in turn actually holds the actual image-link.
                if urllib.parse.urlparse(url).netloc == urllib.parse.urlparse(link_url).netloc:
                    download_images(link_url, debug_statements)

        __RECURSE = True  # Recursion task completed so reset the flag.


def __main__():
    url = "<paste url here>"
    download_images(url, True)
    print(f"total {len(__VISITED_URLS)} urls visited while scrapping {url}.")


if __name__ == "__main__":
    __main__()
