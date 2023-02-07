from bs4 import BeautifulSoup
import requests, re
from os import path, mkdir
import logging

__VISITED_URLS = set()

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)

logHandler = logging.FileHandler('bulk-image-download.log')
logHandler.setFormatter( logging.Formatter('%(asctime)s : [%(levelname)s] - %(message)s\n') )

logger.addHandler(logHandler)

def extractImageName(image_url:str) -> str | None:
    imageName = image_url.split('/')[-1]
    
    name_ext = imageName.rsplit('.',1)
    
    if name_ext[-1].lower() in ('png', 'jpg', 'jpeg', 'bmp', 'gif'):
        return '.'.join(name_ext)
    
        


def downloadImages(url: str, recursiveDownload=False) -> None:
    '''
    - Downloads all the images found on the given url 'url'. 
    - After that, it goes on to download all the images on the links present on the anchor tags in an recursive manner
    if recursiveDownload is True.
    - returns nothing.
    - generates a log of the all the success and errors occuring while downloading
    '''
    if url in __VISITED_URLS:
        # url already visited during a recursive call so ignore the url
        return
    
    print("Scrapping following url:",url)
    
    # mark the url as visited for performing web scrapping operation
    __VISITED_URLS.add(url) 
    try: 
        r = requests.get(url)
        r.raise_for_status() # raises exception if return code is not 200
        logger.info(f'Successfuly retrieved {url}')
        
    except requests.exceptions.SSLError as e:
        # in case a sites SSL verification fails for some reason
        logger.error(f'SSL Error retrieving {url}: {e}')
        verify = input("Retry by ignoring SSL verification (y/n)?: ")
        if verify.lower() == 'y':
            r = requests.get(url, verify=False)
            logger.info(f'Succesfully retrieved {url} with SSL verification ignored')
        else:
            return
    except requests.exceptions.RequestException as e:
        # in case that the request did not return with success code 200 or something else happened
        logger.error(f'Error retrieviing {url}: {e}')
        return
        
    soup = BeautifulSoup(r.content, "html.parser")
    imgs = soup.find_all('img')
    
    # extracting the basepath for the images and the webpage
    # used in-case the extracted url has path relative to base_url folder
    base_url = re.match(r"^(.*/)(.*\.html)?", url).groups()[0]
    
    if not path.exists('images/'):
        mkdir("images/")
    
    print(f"found {len(imgs)} image-links")
    for img in imgs:
        # first checks data-src to see whether lazy-load was used or not.
        # else checks the src tag. If none are found, it returns ''
        imageURL = img.get('data-src', img.get('src', ''))
        parent = img.find_parent('a')
        
        if parent:
            # image was inside an anchor tag so most likely was thumbnail.
            # better to select the link in the anchor tag. if the href field is not found,
            # the link in kept untouched
            imageURL = parent.get('href', imageURL)
        
        if not imageURL: continue # empty url
        
        # formulating complete url for the image if it is given with reference to webpage folder on server
        
        if imageURL[:4] != 'http': 
            imageURL = path.join(base_url, imageURL)
        
        # if protocol still not specified even after joining the url, then we explicitly specify the protocol
        # because with protocol specification, the image cannot be retrieved.
        if imageURL[:4] != 'http': 
            imageURL = 'https:' + imageURL
            
            
        try:
            image_data = requests.get(imageURL) # getting the image data
            image_name = extractImageName(imageURL) # extracting image name from the url
            
            image_data.raise_for_status()
            
            if not image_name: # empty image name so not a valid image url
                continue
            
            with open("images/" + image_name, 'wb') as imageFile:
                imageFile.write(image_data.content)
                
            logger.info(f'Successfuly retrieved image from {imageURL}')
            
        except requests.exceptions.RequestException as e:
            logger.error(f"Error retrieving image from {imageURL}: {e}")

    if recursiveDownload:
        # recursively scrapping all webpages to download the images present on them
        links = soup.find_all('a')
        
        print(f"recursing for {len(links)} links")
        for link in links:
            link_url = link.get('href')
            if link_url:
                if link_url[0:4] != "http":
                    link_url = path.join(base_url, link_url)
                if link_url[0:4] != "http":
                    link_url = "https:" + link_url 
                downloadImages(link_url)
    

def __main__():
    url = "<paste url here>"
    downloadImages(url)
    

if __name__ == "__main__":
    __main__()