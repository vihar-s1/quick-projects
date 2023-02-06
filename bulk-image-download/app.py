from bs4 import BeautifulSoup
import requests, re
from os import path, mkdir

__LOGFILE = 'logFile.txt'

def extractImageName(image_url:str) -> str | None:
    # image = image.replace('%20', '_')  # replace the htmlparsed blank space with underscore
    matchobj = re.findall(r'/([^/]*?)\.(svg|png|jpg|gif|jpeg|webp)', image_url) # extracting image name and extension to save the image
    
    if not matchobj: # no image found
        return None
    # extracting image name and image extension to return the image name
    return f"{matchobj[0][0]}.{matchobj[0][1]}"


def downloadImages(url: str) -> None:
    r = requests.get(url)
    
    logFile = open(__LOGFILE, 'a')
    
    if r.status_code != 200:
        return
    
    soup = BeautifulSoup(r.content, "html.parser")
    imgs = soup.find_all('img')
    
    if not path.exists('images/'):
        mkdir("images/")
    
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

        

def __main__():
    url = "<webpage url>"
    downloadImages(url)
    

if __name__ == "__main__":
    __main__()