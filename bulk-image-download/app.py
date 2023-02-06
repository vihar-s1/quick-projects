from bs4 import BeautifulSoup
import requests, re
from os import path, mkdir

__LOGFILE = 'logFile.txt'
__VISITED_URLS = set()

def extractImageName(image_url:str) -> str | None:
    # image = image.replace('%20', '_')  # replace the htmlparsed blank space with underscore
    matchobj = re.findall(r'/([^/]*?)\.(svg|png|jpg|gif|jpeg|webp)', image_url) # extracting image name and extension to save the image
    
    if not matchobj: # no image found
        return None
    
    # extracting image name and image extension to return the image name
    fileName = f"{matchobj[0][0]}.{matchobj[0][1]}"
    
    return fileName.replace("%20", " ")


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
    
    # mark the url as visited for performing web scrapping operation
    __VISITED_URLS.add(url) 
    
    r = requests.get(url)
    r.raise_for_status()
    
    logFile = open(__LOGFILE, 'a')
    soup = BeautifulSoup(r.content, "html.parser")
    imgs = soup.find_all('img')
    
    if not path.exists('images/'):
        mkdir("images/")
    
    print(f"found {len(imgs)} links")
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
        
        try:
            image_data = requests.get(imageURL) # getting the image data
            image_name = extractImageName(imageURL) # extracting image name from the url
            
            with open("images/" + image_name, 'wb') as imageFile:
                imageFile.write(image_data.content)
                imageFile.close()
                logFile.write(f"[info] : Downloading Successful for image at url:\n{imageURL}\n\n")
        except:
            logFile.write(f"[error] : Failed to download the image at url:\n{imageURL}\n\n")
            
    logFile.close()

    if recursiveDownload:
        # recursively scrapping all webpages to download the images present on them
        links = soup.find_all('a')
        
        for link in links:
            link_url = link.get('href')
            if link_url:
                downloadImages(link_url)
    

def __main__():
    url = "<website-url>"
    downloadImages(url)
    

if __name__ == "__main__":
    __main__()