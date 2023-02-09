from bs4 import BeautifulSoup
import requests, logging, urllib.parse
from os import path, mkdir, system


__VISITED_URLS = set()
__RECURSE = True # flag to ensure single level recursion

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
    
        

def downloadImages(url: str, debugStatements=False) -> None:
    '''
    - Downloads all the images found on the given url 'url'.
    - prints minimal debug statements if debugStatements = true
    - returns nothing.
    - generates a log of the all the success and errors occuring while downloading
    '''
    global __RECURSE
    if url in __VISITED_URLS:
        # url already visited during a recursive call so ignore the url
        return
    
    if debugStatements:
        print("\nScrapping following url:",url)
        
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
    
    if not path.exists('images/'):
        mkdir("images/")
    
    if debugStatements:
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
        
        # Accounting for the case that the image url is actually relative path on the server
        # netloc (network location) will be None in such case
        if not urllib.parse.urlparse(imageURL).netloc:
            imageURL = urllib.parse.urljoin(url, imageURL) # join url to imageURL to get absolute image URL
            
        if not urllib.parse.urlparse(imageURL).scheme:
            imageURL = "https:" + imageURL
            
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

    if __RECURSE:
        # performing first level recursion
        __RECURSE = False # setting recursion flag to false to prevent further recursive calls.
        
        links = soup.find_all('a')
        
        if debugStatements:
            print(f"\nrecursing for {len(links)} links\n")
            
        for i in range(len(links)):
            link_url = links[i].get('href')
            
            if link_url:
                if debugStatements:
                    print(f"\n{i}. Recursing for {link_url}")  
                    
                if not urllib.parse.urlparse(link_url).netloc:
                    link_url = urllib.parse.urljoin(url, link_url)
                    
                # Scrapping 1 level deep in case the image anchor tags contain a link to web-page 
                # which in turn actually holds the actual image-link.
                downloadImages(link_url, debugStatements)
    

def __main__():
    url = "<paste url here>"
    downloadImages(url, True)
    print(f"total {len(__VISITED_URLS)} urls visited while scrapping {url}.")
    

if __name__ == "__main__":
    __main__()